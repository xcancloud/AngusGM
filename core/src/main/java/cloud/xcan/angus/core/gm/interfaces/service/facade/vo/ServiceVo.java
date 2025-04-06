package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import cloud.xcan.angus.api.commonlink.service.ServiceSource;
import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class ServiceVo {

  private Long id;

  private String name;

  private String code;

  private String description;

  private String url;

  private String healthUrl;

  private ServiceSource source;

  private Boolean enabled;

  private Long apiNum;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private LocalDateTime createdDate;

  private Long lastModifiedBy;

  @NameJoinField(id = "lastModifiedBy", repository = "commonUserBaseRepo")
  private String lastModifiedName;

  private LocalDateTime lastModifiedDate;

}
