package cloud.xcan.angus.api.gm.tenant.to.audit;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuditRecordTo implements Serializable {

  private String reason;

  private LocalDateTime auditDate;

  private Long auditUserId;

  private String auditUserName;

}
