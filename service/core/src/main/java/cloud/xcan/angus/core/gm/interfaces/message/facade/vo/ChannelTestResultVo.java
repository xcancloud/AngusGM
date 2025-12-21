package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 渠道测试结果VO
 */
@Data
@Schema(description = "渠道测试结果")
public class ChannelTestResultVo {
    
    @Schema(description = "渠道ID", example = "CH001")
    private String channelId;
    
    @Schema(description = "测试时间")
    private LocalDateTime testTime;
    
    @Schema(description = "是否成功", example = "true")
    private Boolean success;
    
    @Schema(description = "消息", example = "测试消息已发送")
    private String message;
}
