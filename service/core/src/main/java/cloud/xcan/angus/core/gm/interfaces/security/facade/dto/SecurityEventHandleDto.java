package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "安全事件处理DTO")
public class SecurityEventHandleDto {
    
    @Schema(description = "处理备注", example = "已确认为正常行为，用户忘记密码")
    private String handledNote;
}
