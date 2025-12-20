package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuditLogUpdateDto implements Serializable {
    private String name;
    private String description;
}
