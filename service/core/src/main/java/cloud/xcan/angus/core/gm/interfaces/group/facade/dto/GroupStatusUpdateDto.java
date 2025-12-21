package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group status update DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupStatusUpdateDto implements Serializable {

  @NotBlank
  private String status;
}
