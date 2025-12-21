package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Service instance status update DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ServiceInstanceStatusDto implements Serializable {

  @NotBlank
  private String status;
}
