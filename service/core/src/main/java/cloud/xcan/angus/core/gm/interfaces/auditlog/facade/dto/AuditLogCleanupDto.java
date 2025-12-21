package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "审计日志清理DTO")
public class AuditLogCleanupDto implements Serializable {
    
    @NotBlank
    @Schema(description = "清理日期之前的日志")
    private String beforeDate;
    
    @Schema(description = "日志级别（仅清理指定级别）")
    private String level;
}
