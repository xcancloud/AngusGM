package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class AuditLogCreateDto implements Serializable {
    private String name;
    private String description;
    private Boolean enabled = true;
}
