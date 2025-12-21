package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知历史查询DTO
 */
@Data
@Schema(description = "通知历史查询请求")
public class NotificationHistoryFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "渠道ID筛选", example = "CH001")
    private String channelId;
    
    @Schema(description = "状态筛选", example = "success")
    private String status;
}
