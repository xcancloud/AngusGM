package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "备份内容")
public class BackupContentVo {
    
    @Schema(description = "是否包含数据库")
    private Boolean database;
    
    @Schema(description = "是否包含文件")
    private Boolean files;
    
    @Schema(description = "是否包含配置")
    private Boolean configurations;
}