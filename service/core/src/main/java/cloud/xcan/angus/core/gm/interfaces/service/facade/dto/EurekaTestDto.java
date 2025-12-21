package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Eureka connection test DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class EurekaTestDto implements Serializable {

  private String serviceUrl;

  private String username;

  private String password;
}
