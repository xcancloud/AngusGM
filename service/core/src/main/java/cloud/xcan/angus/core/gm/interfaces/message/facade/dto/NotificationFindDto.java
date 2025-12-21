package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知查询DTO
 */
@Data
@Schema(description = "通知查询请求")
public class NotificationFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "搜索关键词", example = "租户")
    private String keyword;
    
    @Schema(description = "分类筛选", example = "unread")
    private String category;
    
    @Schema(description = "来源筛选", example = "Billing System")
    private String source;
    
    @Schema(description = "状态筛选", example = "warning")
    private String status;
}
