package cloud.xcan.angus.core.gm.domain.tenant.audit;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AuditRecordData extends ValueObjectSupport<AuditRecordData> {

  private String reason;

  private LocalDateTime auditDate;

  private Long auditUserId;

  private String auditUserName;

}
