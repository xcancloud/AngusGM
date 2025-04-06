package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.spec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class ServiceApiVo {

  private Long id;

  private String name;

  private String code;

  private String uri;

  private HttpMethod method;

  private ApiType type;

  private String description;

  private String resourceName;

  private String serviceName;

  private Boolean enabled;

}
