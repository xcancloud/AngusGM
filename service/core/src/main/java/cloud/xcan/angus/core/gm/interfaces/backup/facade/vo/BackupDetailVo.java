package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "备份详情")
public class BackupDetailVo extends TenantAuditingVo {
    @Schema(description = "备份ID")
    private Long id;
    
    @Schema(description = "备份名称")
    private String name;
    
    @Schema(description = "备份类型")
    private BackupType type;
    
    @Schema(description = "备份状态")
    private BackupStatus status;
    
    @Schema(description = "源路径")
    private String sourcePath;
    
    @Schema(description = "备份路径")
    private String backupPath;
    
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
    
    @Schema(description = "保留天数")
    private Integer retentionDays;
    
    @Schema(description = "是否自动删除")
    private Boolean autoDelete;
    
    @Schema(description = "是否已验证")
    private Boolean verified;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "备份内容")
    private BackupContentVo backupContent;
    
    @Schema(description = "是否可恢复")
    private Boolean canRestore;
    
    @Schema(description = "恢复历史记录")
    private List<RestoreHistoryVo> restoreHistory;
}
