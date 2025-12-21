package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Application status update DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ApplicationStatusUpdateDto implements Serializable {

  @NotBlank
  private String status;
}
