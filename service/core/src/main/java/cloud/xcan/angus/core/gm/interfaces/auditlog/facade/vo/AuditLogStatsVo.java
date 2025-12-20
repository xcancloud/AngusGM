package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuditLogStatsVo implements Serializable {
    private Long total;
    private Long enabled;
    private Long disabled;
}
