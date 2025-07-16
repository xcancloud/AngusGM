package cloud.xcan.angus.core.gm.interfaces.auth.facade.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthClientFindDto {

  private String id;

  private String clientId;

  private String tenantId;

}
