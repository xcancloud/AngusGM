package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询备份记录请求参数")
public class BackupFindDto extends PageQuery {
    @Schema(description = "搜索关键词")
    private String keyword;
    
    @Schema(description = "备份名称")
    private String name;
    
    @Schema(description = "备份类型")
    private BackupType type;
    
    @Schema(description = "备份状态")
    private BackupStatus status;
    
    @Schema(description = "开始日期")
    private LocalDateTime startDate;
    
    @Schema(description = "结束日期")
    private LocalDateTime endDate;
}
