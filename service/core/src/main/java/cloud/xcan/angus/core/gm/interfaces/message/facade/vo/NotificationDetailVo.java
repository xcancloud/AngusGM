package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知详情VO
 */
@Data
@Schema(description = "通知详情")
public class NotificationDetailVo {
    
    @Schema(description = "租户ID")
    private String tenantId;
    
    @Schema(description = "创建人ID")
    private String createdBy;
    
    @Schema(description = "创建人")
    private String creator;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
    
    @Schema(description = "修改人ID")
    private String modifiedBy;
    
    @Schema(description = "修改人")
    private String modifier;
    
    @Schema(description = "修改时间")
    private LocalDateTime modifiedDate;
    
    @Schema(description = "通知ID", example = "NOTIF001")
    private String id;
    
    @Schema(description = "标题", example = "租户 TechFlow Inc 升级为企业版")
    private String title;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "来源", example = "Subscription Service")
    private String source;
    
    @Schema(description = "分类", example = "all")
    private String category;
    
    @Schema(description = "状态", example = "success")
    private String status;
    
    @Schema(description = "是否已读", example = "true")
    private Boolean isRead;
    
    @Schema(description = "是否收藏", example = "false")
    private Boolean isStarred;
    
    @Schema(description = "是否归档", example = "false")
    private Boolean isArchived;
    
    @Schema(description = "时间")
    private LocalDateTime time;
    
    @Schema(description = "标签")
    private List<String> tags;
    
    @Schema(description = "元数据")
    private Map<String, Object> metadata;
}
