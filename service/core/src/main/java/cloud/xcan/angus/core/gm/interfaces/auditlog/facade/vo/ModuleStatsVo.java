package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "模块统计VO")
public class ModuleStatsVo implements Serializable {
    
    @Schema(description = "模块")
    private String module;
    
    @Schema(description = "模块名称")
    private String moduleName;
    
    @Schema(description = "总操作次数")
    private Long totalOperations;
    
    @Schema(description = "创建操作次数")
    private Long create;
    
    @Schema(description = "更新操作次数")
    private Long update;
    
    @Schema(description = "删除操作次数")
    private Long delete;
    
    @Schema(description = "查询操作次数")
    private Long query;
}
