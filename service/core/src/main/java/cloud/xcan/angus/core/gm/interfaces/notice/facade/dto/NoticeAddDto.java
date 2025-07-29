package cloud.xcan.angus.core.gm.interfaces.notice.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_REMARK_LENGTH;

import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.notice.NoticeScope;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
public class NoticeAddDto implements Serializable {

  @NotEmpty
  @Length(max = MAX_REMARK_LENGTH)
  @Schema(description = "Notification content message", requiredMode = RequiredMode.REQUIRED)
  private String content;

  @NotNull
  @Schema(description = """
      Notification scope for targeting recipients:
      - ***GLOBAL***: Send to all applications and users
      - ***APP***: Send only to specified application users""", requiredMode = RequiredMode.REQUIRED)
  private NoticeScope scope;

  @Schema(description = "Application identifier for APP scope notifications. Required when scope=APP")
  private Long appId;

  @NotNull
  @Schema(description = """
      Notification delivery timing:
      - ***SEND_NOW***: Deliver notification immediately after creation
      - ***TIMING_SEND***: Schedule notification for delivery at specified time""",
      requiredMode = RequiredMode.REQUIRED)
  private SentType sendType;

  @Future
  @Schema(description = "Scheduled delivery timestamp. Required when sendType=TIMING_SEND")
  private LocalDateTime sendTimingDate;

  @Future
  @NotNull
  @Schema(description = "Notification expiration timestamp when content will no longer be displayed", requiredMode = RequiredMode.REQUIRED)
  private LocalDateTime expirationDate;

}
