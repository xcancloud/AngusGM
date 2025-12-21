package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * Application status update DTO
 * </p>
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新应用状态请求参数")
public class ApplicationStatusUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
  private ApplicationStatus status;
}
