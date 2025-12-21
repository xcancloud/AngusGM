package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "恢复历史记录")
public class RestoreHistoryVo {
    
    @Schema(description = "恢复时间")
    private LocalDateTime restoreTime;
    
    @Schema(description = "恢复操作人")
    private String restoreBy;
    
    @Schema(description = "恢复状态")
    private String restoreStatus;
}