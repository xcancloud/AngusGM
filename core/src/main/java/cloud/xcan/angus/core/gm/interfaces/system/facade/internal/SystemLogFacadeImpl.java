package cloud.xcan.angus.core.gm.interfaces.system.facade.internal;

import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemLogFacade;
import cloud.xcan.angus.spec.http.HttpSender;
import cloud.xcan.angus.spec.http.HttpSender.Response;
import cloud.xcan.angus.spec.http.HttpUrlConnectionSender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SystemLogFacadeImpl implements SystemLogFacade {

  private final HttpSender httpSender = new HttpUrlConnectionSender(Duration.ofSeconds(3),
      Duration.ofSeconds(120));

  @Resource
  private ObjectMapper objectMapper;

  @Override
  public List<String> fileList(String instanceId) {
    try {
      Response response = httpSender.get(String.format("http://%s/actuator/systemlog", instanceId))
          .send();
      if (response.isSuccessful()) {
        String body = response.body();
        return objectMapper.readValue(body, new TypeReference<>() {
        });
      }
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
    }
    return Collections.emptyList();
  }

  @Override
  public String fileDetail(String instanceId, String fileName, Integer linesNum, Boolean tail) {
    try {
      Response response = httpSender.get(String.format("http://%s/actuator/systemlog/name",
          instanceId) + assembleQueryParams(fileName, linesNum, tail)).send();
      if (response.isSuccessful()) {
        return response.body();
      }
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
    }
    return "";
  }

  @Override
  public void fileClear(String instanceId, String fileName) {
    try {
      httpSender.post(String.format("http://%s/actuator/systemlog/name",
          instanceId) + assembleQueryParams(fileName)).send();
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
    }
  }

  private String assembleQueryParams(String fileName, Integer linesNum, Boolean tail) {
    StringBuilder sb = new StringBuilder("?name=").append(fileName);
    if (Objects.nonNull(linesNum)) {
      sb.append("&linesNum=").append(linesNum);
    }
    sb.append("&tail=").append(Objects.nonNull(tail) ? tail : true);
    return sb.toString();
  }

  private String assembleQueryParams(String fileName) {
    return "?name=" + fileName;
  }
}
