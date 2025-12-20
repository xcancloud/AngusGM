package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuditLogListVo implements Serializable {
    private Long id;
    private String name;
    private Boolean enabled;
}
