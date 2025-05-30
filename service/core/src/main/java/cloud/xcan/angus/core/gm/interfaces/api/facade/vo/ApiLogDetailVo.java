package cloud.xcan.angus.core.gm.interfaces.api.facade.vo;

import cloud.xcan.angus.api.enums.ApiType;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.LinkedMultiValueMap;


@Setter
@Getter
@Accessors(chain = true)
public class ApiLogDetailVo implements Serializable {

  private Long id;
  private String clientId;
  private String clientSource;
  private String remote;
  private ApiType apiType;
  private Long tenantId;
  private String tenantName;
  private Long userId;
  private String fullName;
  private String requestId;
  private String serviceCode;
  private String serviceName;
  private String instanceId;
  private String apiCode;
  private String apiName;
  private String resourceName;
  private String method;
  private String uri;
  private LocalDateTime requestDate;
  private Integer status;
  private String queryParam;
  private LinkedMultiValueMap<String, String> requestHeaders;
  private String requestBody;
  private LocalDateTime responseDate;
  private Integer responseSize;
  private LinkedMultiValueMap<String, String> responseHeaders;
  private Integer requestSize;
  private String responseBody;
  private Boolean success;
  private Long elapsedMillis;
  private LocalDateTime createdDate;

}
