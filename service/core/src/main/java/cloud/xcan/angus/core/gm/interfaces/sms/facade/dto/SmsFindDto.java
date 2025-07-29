package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

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
public class SmsFindDto extends PageQuery {

  @Schema(description = "SMS message unique identifier for filtering")
  private Long id;

  @Schema(description = "SMS template identifier code for filtering")
  private String templateCode;

  @Schema(description = "SMS message language for filtering")
  private String language;

  @Schema(description = "SMS business type for filtering")
  private SmsBizKey bizKey;

  @Schema(description = "Verification code SMS flag for filtering")
  private Boolean verificationCode;

  @Schema(description = "Batch SMS flag for filtering")
  private Boolean batch;

  @Schema(description = "Sending tenant identifier for filtering")
  private Long sendTenantId;

  @Schema(description = "Sending user identifier for filtering")
  private Long sendUserId;

  @Schema(description = "Urgent SMS flag for filtering")
  private Boolean urgent;

  @Schema(description = "SMS sending process status for filtering")
  private ProcessStatus sendStatus;

  @Schema(description = "External SMS identifier for filtering")
  private String outId;

  @Schema(description = "Actual SMS sending date for filtering")
  private LocalDateTime actualSendDate;

  @Schema(description = "Expected SMS sending date for filtering")
  private LocalDateTime expectedSendDate;

}
