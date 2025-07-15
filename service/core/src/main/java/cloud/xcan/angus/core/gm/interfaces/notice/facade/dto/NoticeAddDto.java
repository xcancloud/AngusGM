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
  @Schema(description = "Notice content", maxLength = MAX_REMARK_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String content;

  @NotNull
  @Schema(description = """
      Notice scope, supported scope:
      - ***GLOBAL***: Notify within all applications.
      - ***APP***: Notify within the specified application""", requiredMode = RequiredMode.REQUIRED)
  private NoticeScope scope;

  @Schema(description = "Notice application id, it is required when scope=APP")
  private Long appId;

  @NotNull
  @Schema(description = """
      Send type, supported type:
      - ***SEND_NOW***: Send the notification immediately after creation.
      - ***TIMING_SEND***: Schedule the notification to be sent at a specified time""",
      requiredMode = RequiredMode.REQUIRED)
  private SentType sendType;

  @Future
  @Schema(description = "Schedule the notification to be sent at a specified time, it is required when sendType=TIMING_SEND")
  private LocalDateTime sendTimingDate;

  @Future
  @NotNull
  @Schema(description = "The notification content displays the expiration time", requiredMode = RequiredMode.REQUIRED)
  private LocalDateTime expirationDate;

}
