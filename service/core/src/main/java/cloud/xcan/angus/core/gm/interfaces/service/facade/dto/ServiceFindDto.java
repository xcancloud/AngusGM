package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import cloud.xcan.angus.api.commonlink.service.ServiceSource;
import cloud.xcan.angus.remote.PageQuery;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class ServiceFindDto extends PageQuery {

  @Id
  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private ServiceSource source;
}
