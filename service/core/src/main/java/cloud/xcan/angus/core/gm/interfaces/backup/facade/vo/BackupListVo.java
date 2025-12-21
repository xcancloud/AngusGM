package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "备份列表项")
public class BackupListVo extends TenantAuditingVo {
    @Schema(description = "备份ID")
    private Long id;
    
    @Schema(description = "备份名称")
    private String name;
    
    @Schema(description = "备份类型")
    private BackupType type;
    
    @Schema(description = "备份状态")
    private BackupStatus status;
    
    @Schema(description = "文件大小")
    private Long fileSize;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
