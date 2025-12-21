package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * Application status update response VO
 * </p>
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "应用状态更新响应")
public class ApplicationStatusUpdateVo implements Serializable {

  @Schema(description = "应用ID")
  private Long id;

  @Schema(description = "状态")
  private ApplicationStatus status;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
