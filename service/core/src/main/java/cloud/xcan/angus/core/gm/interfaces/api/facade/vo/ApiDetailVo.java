package cloud.xcan.angus.core.gm.interfaces.api.facade.vo;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.spec.http.HttpMethod;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class ApiDetailVo {

  private Long id;

  private String name;

  private String operationId;

  private String uri;

  private HttpMethod method;

  private ApiType type;

  private String description;

  private String resourceName;

  // TODO 国际化
  private String resourceDescription;

  private Long serviceId;

  private String serviceCode;

  private String serviceName;

  private Boolean enabled;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

  private Long modifiedBy;

  @NameJoinField(id = "modifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedName;

  private LocalDateTime modifiedDate;

}
