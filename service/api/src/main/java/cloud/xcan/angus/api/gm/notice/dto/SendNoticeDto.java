package cloud.xcan.angus.api.gm.notice.dto;

import cloud.xcan.angus.api.enums.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SendNoticeDto implements Serializable {

  @NotNull
  @Size(min = 1, max = 3)
  @Schema(description = "Notification method types for multi-channel message delivery. Supports 1-3 different notification channels", requiredMode = RequiredMode.REQUIRED)
  private List<NoticeType> noticeTypes;

  @Valid
  @Schema(description = "SMS notification parameters. Required when NoticeType includes SMS for SMS message delivery")
  private SendSmsParam sendSmsParam;

  @Valid
  @Schema(description = "Email notification parameters. Required when NoticeType includes EMAIL for email message delivery")
  private SendEmailParam sendEmailParam;

  @Valid
  @Schema(description = "In-site notification parameters. Required when NoticeType includes IN_SITE for internal message delivery")
  private SendInsiteParam sendInsiteParam;

}


