package cloud.xcan.angus.core.gm.infra.ai.supplier;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.domain.ai.AIChatCreateSessionVo;
import cloud.xcan.angus.core.gm.infra.ai.AIChatType;
import cloud.xcan.angus.core.gm.infra.ai.cases.FuncCaseVo;
import cloud.xcan.angus.spec.http.HttpMethod;
import cloud.xcan.angus.spec.http.HttpSender;
import cloud.xcan.angus.spec.http.HttpUrlConnectionSender;
import cloud.xcan.angus.spec.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.lang.Nullable;

@Slf4j
public class TudouAIAgentClient implements AIAgentClient {

  @Override
  public String createSession(String aiAgentId) throws Exception {
    HttpSender.Response response = new HttpUrlConnectionSender().send(
        new HttpSender.Request(new URL(format(
            "https://agentos.i-tudou.com/api-%s/v1/api/agents/2137107453692846364/sessions",
            aiAgentId)), null, HttpMethod.POST,
            Map.of("satoken", "", "version", "15", "officeid", ""), null));
    ProtocolAssert.assertTrue(response.isSuccessful(),
        "Failed to create a session, cause: " + response.body());
    AIChatCreateSessionVo sessionVo = JsonUtils.convert(response.body(),
        AIChatCreateSessionVo.class);
    ProtocolAssert.assertTrue(sessionVo != null && sessionVo.getData() != null
            && sessionVo.getData().getId() != null,
        "Failed to parse session info, body: " + response.body());
    return sessionVo.getData().getId();
  }

  @Override
  public Map<String, Object> chat(AIChatType type, String cmd, String sessionId, String aiAgentId) {
    String message = assembleChatMessage(type, cmd);
    long startTime = System.nanoTime();
    log.info("User {}-{} chat message: {}", getUserId(), sessionId, message);

    Request request = assembleChatRequest(sessionId, aiAgentId, message);

    OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(30,
            TimeUnit.SECONDS).readTimeout(1, TimeUnit.MINUTES)
        .build();

    CountDownLatch eventLatch = new CountDownLatch(1);

    Map<String, Object> result = new HashMap<>();
    RealEventSource eventSource = handleEventMessage(type, sessionId, request, eventLatch, result);
    try {
      eventSource.connect(okHttpClient);
      eventLatch.await();
      log.info("User {}-{} chat time taken: {}ms", getUserId(), sessionId,
          (System.nanoTime() - startTime) / 1_000_000);
    } catch (InterruptedException e) {
      log.error("" + e);
    }
    okHttpClient.dispatcher().executorService().shutdown();
    return result;
  }

  private static RealEventSource handleEventMessage(AIChatType chatType, String sessionId,
      Request request, CountDownLatch eventLatch, Map<String, Object> result) {
    return new RealEventSource(request, new EventSourceListener() {
      Boolean holdContentFinish = null;
      final StringBuilder finalNormal = new StringBuilder();

      @Override
      public void onFailure(EventSource eventSource, @Nullable Throwable t,
          @Nullable Response response) {
        try {
          String message = nonNull(t)
              ? format("Send AI chat message failed, cause: %s", t.getMessage())
              : nonNull(response) ? format(
                  "Send AI chat message failed, status: %s, message:%s, body:%s", response.code(),
                  response.message(), nonNull(response.body()) ? response.body().string()
                      : "Unknown exception")
                  : "Unknown exception";
          log.error(message);
          result.put("error", message);
        } catch (Exception e) {
          // NOOP
        }
        eventLatch.countDown();
        super.onFailure(eventSource, t, response);
      }

      @Override
      public void onOpen(EventSource eventSource, Response response) {
        super.onOpen(eventSource, response);
      }

      @Override
      public void onClosed(EventSource eventSource) {
        log.info("User {}-{} chat closed", sessionId, getUserId());
        eventLatch.countDown();
        super.onClosed(eventSource);
      }

      @Override
      public void onEvent(EventSource eventSource, String id, String type, String data) {
        Map<String, Object> objectMap;
        try {
          objectMap = JsonUtils.readValue(data);
          if (isNull(objectMap) || !objectMap.containsKey("message_type")) {
            result.put("error", "Parsing event json failed");
            eventLatch.countDown();
            return;
          }
        } catch (Exception e) {
          log.error("Parse AI chat message json failed", e);
          result.put("error", e.getMessage());
          eventLatch.countDown();
          return;
        }

        String messageType = objectMap.get("message_type").toString();
        String streamStatus = objectMap.get("stream_status").toString();
        if ("perception_summary_content".equals(messageType)) {
          if (isNull(holdContentFinish)) {
            holdContentFinish = false;
          }
          Map<String, Object> data0 = (Map<String, Object>) objectMap.get("data");
          if (nonNull(data0)) {
            Map<String, Object> chatArea = (Map<String, Object>) data0.get("chat_area");
            if (nonNull(chatArea)) {
              String normal = chatArea.get("normal").toString();
              if (isNotEmpty(normal)) {
                finalNormal.append(normal);
              }
            }
          }
        } else {
          // holdContentFinish = false means that the content part is currently being processed
          if (nonNull(holdContentFinish)
              && !holdContentFinish/* && "finish".equals(streamStatus)*/) {
            holdContentFinish = true;
          }
        }
        if (nonNull(holdContentFinish) && holdContentFinish) {
          result.put("normal", finalNormal.toString());
          log.info("User {}-{} chat result: {}", getUserId(), sessionId, finalNormal.toString());
          String content = chatType.isYamlFormat() ? extractYamlContent(finalNormal.toString())
              : extractJsonContent(finalNormal.toString());
          if (nonNull(content) && holdContentFinish) {
            try {
              switch (chatType) {
                case WRITE_BACKLOG:
                case SPLIT_SUB_TASK:
                  result.put("content",
                      JsonUtils.convert(content, new TypeReference<List<String>>() {
                      }));
                  break;
                case WRITE_FUNC_CASE:
                  result.put("content",
                      JsonUtils.convert(content, new TypeReference<List<FuncCaseVo>>() {
                      }));
                  break;
                case WRITE_ANGUS_SCRIPT:
                  result.put("content", content);
                  break;
              }

            } catch (JsonProcessingException e) {
              result.put("error", "Parsing normal content failure, cause: " + e.getMessage());
            }
          }
          eventLatch.countDown();
        }
      }
    });
  }

  private static Request assembleChatRequest(String sessionId, String aiAgentId,
      String message) {
    MediaType json = MediaType.parse("application/json; charset=utf-8");
    RequestBody requestBody = RequestBody.create(json, message);
    return new Request.Builder().url(
            format(
                "https://agentos.i-tudou.com/api-%s/v1/api/agents/2137107453692846364/sessions/%s/messages",
                aiAgentId, sessionId))
        .post(requestBody)
        .addHeader("satoken", "")
        .addHeader("version", "15")
        .addHeader("officeid", "")
        .addHeader("Accept", "text/event-stream")
        .build();
  }

  private static String assembleChatMessage(AIChatType type, String cmd) {
    String finalCmd = cmd;
    switch (type) {
      case WRITE_BACKLOG:
        finalCmd += WRITE_BACKLOG_PROMPT;
        break;
      case SPLIT_SUB_TASK:
        finalCmd += SPLIT_SUB_TASK_PROMPT;
        break;
      case WRITE_FUNC_CASE:
        finalCmd += WRITE_FUNC_CAE_PROMPT;
        break;
      case WRITE_ANGUS_SCRIPT:
        finalCmd += WRITE_ANGUS_SCRIPT_PROMPT;
        break;
    }
    Map<String, Object> message = new HashMap<>();
    message.put("role", "human");
    message.put("chat_area", Map.of("normal", finalCmd));
    message.put("perception_stream", "false");
    return JsonUtils.toJson(message);
  }

  private static String extractJsonContent(String input) {
    String regex = "```json\\s*(.*?)\\s*```";
    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(input);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  private static String extractYamlContent(String input) {
    String regex = "```yaml\\s*(.*?)\\s*```";
    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(input);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

}
