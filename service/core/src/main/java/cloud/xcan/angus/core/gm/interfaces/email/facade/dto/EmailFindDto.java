package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ProcessStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EmailFindDto extends PageQuery {

  @Schema(description = "Email identifier")
  private Long id;

  @Schema(description = "Email template code for filtering")
  private String templateCode;

  @Schema(description = "Email language for filtering")
  private String language;

  @Schema(description = "Business key for filtering")
  private SmsBizKey bizKey;

  @Schema(description = "External identifier for filtering")
  private String outId;

  @Schema(description = "Email type classification")
  private EmailType emailType;

  @Schema(description = "Whether email content is HTML format")
  private Boolean html;

  @Schema(description = "Whether this is a verification code email")
  private Boolean verificationCode;

  @Schema(description = "Whether this is a batch email")
  private Boolean batch;

  @Schema(description = "Sender tenant identifier")
  private Long sendTenantId;

  @Schema(description = "Sender user identifier")
  private Long sendUserId;

  @Schema(description = "Whether this is an urgent email")
  private Boolean urgent;

  @Schema(description = "Email sending status")
  private ProcessStatus sendStatus;

  @Schema(description = "Expected email sending timestamp")
  private LocalDateTime expectedSendDate;

  @Schema(description = "Actual email sending timestamp")
  private LocalDateTime actualSendDate;

}
