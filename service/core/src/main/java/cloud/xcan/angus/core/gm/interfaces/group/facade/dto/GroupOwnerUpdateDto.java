package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Group owner update DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class GroupOwnerUpdateDto implements Serializable {

  @NotNull
  private Long ownerId;
}
