package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "邮件模板状态更新响应VO")
public class EmailTemplateStatusVo {
    
    @Schema(description = "模板ID")
    private Long id;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "修改时间")
    private LocalDateTime modifiedDate;
}
