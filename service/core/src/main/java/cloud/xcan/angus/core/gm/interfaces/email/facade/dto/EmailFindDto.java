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

  private Long id;

  private String templateCode;

  private String language;

  private SmsBizKey bizKey;

  private String outId;

  private EmailType emailType;

  private Boolean html;

  private Boolean verificationCode;

  private Boolean batch;

  private Long sendTenantId;

  private Long sendUserId;

  private Boolean urgent;

  private ProcessStatus sendStatus;

  private LocalDateTime expectedSendDate;

  private LocalDateTime actualSendDate;

}
