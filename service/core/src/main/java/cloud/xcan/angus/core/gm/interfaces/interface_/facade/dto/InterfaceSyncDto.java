package cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface sync DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceSyncDto implements Serializable {

  @NotBlank
  private String serviceName;
}
