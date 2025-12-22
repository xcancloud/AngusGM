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
    
    @Schema(description = "文件大小（格式化的字符串，如：2.5 GB）")
    private String size;
    
    @Schema(description = "备份文件路径")
    private String path;
    
    @Schema(description = "持续时间（格式化的字符串，如：15分钟）")
    private String duration;
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "文件大小（字节数）")
    private Long fileSize;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
