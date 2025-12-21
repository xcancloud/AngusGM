package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知规则VO
 */
@Data
@Schema(description = "通知规则")
public class NotificationRuleVo {
    
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
    
    @Schema(description = "规则ID", example = "RULE001")
    private String id;
    
    @Schema(description = "规则名称", example = "用户登录失败告警")
    private String name;
    
    @Schema(description = "事件类型", example = "user.login.failed")
    private String event;
    
    @Schema(description = "渠道ID列表")
    private List<String> channels;
    
    @Schema(description = "条件表达式", example = "attempts >= 3")
    private String conditions;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;
}
