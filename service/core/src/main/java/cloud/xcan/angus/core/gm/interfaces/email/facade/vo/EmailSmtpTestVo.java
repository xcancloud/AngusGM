package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "SMTP连接测试响应VO")
public class EmailSmtpTestVo {
    
    @Schema(description = "是否连接成功")
    private Boolean connected;
    
    @Schema(description = "测试时间")
    private LocalDateTime testTime;
    
    @Schema(description = "测试消息")
    private String message;
}
