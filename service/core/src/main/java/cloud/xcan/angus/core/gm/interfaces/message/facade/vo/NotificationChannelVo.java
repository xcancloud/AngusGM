package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知渠道VO
 */
@Data
@Schema(description = "通知渠道")
public class NotificationChannelVo {
    
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
    
    @Schema(description = "渠道ID", example = "CH001")
    private String id;
    
    @Schema(description = "渠道名称", example = "System Email")
    private String name;
    
    @Schema(description = "渠道类型", example = "email")
    private String type;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
    
    @Schema(description = "统计信息")
    private ChannelStats stats;
    
    @Schema(description = "配置信息")
    private Map<String, String> config;
    
    @Schema(description = "是否验证通过", example = "true")
    private Boolean verified;
    
    /**
     * 渠道统计
     */
    @Data
    @Schema(description = "渠道统计")
    public static class ChannelStats {
        @Schema(description = "已发送数量", example = "12370")
        private Integer sent;
        
        @Schema(description = "最后发送时间")
        private LocalDateTime lastSent;
    }
}
