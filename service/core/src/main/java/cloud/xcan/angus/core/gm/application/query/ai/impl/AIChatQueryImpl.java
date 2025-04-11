package cloud.xcan.angus.core.gm.application.query.ai.impl;

import static cloud.xcan.angus.core.gm.domain.ThirdCoreMessage.AI_AGENT_NOT_ENABLED;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.aiagent.AIAgent;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.query.ai.AIChatQuery;
import cloud.xcan.angus.core.gm.infra.ai.AIAgentClientFactory;
import cloud.xcan.angus.core.gm.infra.ai.AIChatType;
import cloud.xcan.angus.core.gm.infra.ai.supplier.AIAgentClient;
import cloud.xcan.angus.core.gm.infra.ai.supplier.ProviderType;
import cloud.xcan.angus.lettucex.util.RedisService;
import jakarta.annotation.Resource;
import java.util.Map;
import lombok.SneakyThrows;

@Biz
public class AIChatQueryImpl implements AIChatQuery {

  private static final String USER_CHAT_SESSION_KEY = "aichat:user_chat_session_key:%s";

  @Resource
  private SettingManager settingManager;

  @Resource
  private RedisService<String> stringRedisService;

  @Override
  public Map<String, Object> chatResult(AIChatType type, String question) {
    return new BizTemplate<Map<String, Object>>() {

      @SneakyThrows
      @Override
      protected Map<String, Object> process() {
        AIAgent aiAgent = settingManager.setting(SettingKey.AI_AGENT).getAiAgent();
        ProtocolAssert.assertNotNull(aiAgent, AI_AGENT_NOT_ENABLED);
        ProtocolAssert.assertTrue(aiAgent.isEnabled(), AI_AGENT_NOT_ENABLED);

        AIAgentClient client = AIAgentClientFactory.create(
            ProviderType.valueOf(aiAgent.getProvider()));

        // Query the created session ID
        String cacheSessionKey = String.format(USER_CHAT_SESSION_KEY, getUserId());
        String sessionId = stringRedisService.get(cacheSessionKey);
        if (isEmpty(sessionId)) {
          // Create a session
          ProtocolAssert.assertNotNull(aiAgent.getAgentId(), "AI agent id is null");
          sessionId = client.createSession(aiAgent.getAgentId());
          // Cache session id
          stringRedisService.set(cacheSessionKey, sessionId);
        }

        // Query chat question result
        Map<String, Object> result = client.chat(type, question, sessionId, aiAgent.getAgentId());
        if (result.containsKey("error")) {
          ProtocolAssert.assertTrue(true, result.get("error").toString());
        }
        ProtocolAssert.assertTrue(result.containsKey("content"), "Chat message content is null");
        return result;
      }
    }.execute();
  }

}
