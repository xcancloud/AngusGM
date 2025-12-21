package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 版本历史VO
 */
@Data
@Schema(description = "版本历史")
public class VersionHistoryVo {
    
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
    
    @Schema(description = "版本ID", example = "VER_001")
    private String id;
    
    @Schema(description = "版本号", example = "1.5.2")
    private String version;
    
    @Schema(description = "构建号", example = "20251219001")
    private String buildNumber;
    
    @Schema(description = "发布日期", example = "2025-12-15")
    private String releaseDate;
    
    @Schema(description = "发布类型", example = "patch")
    private String releaseType;
    
    @Schema(description = "标题", example = "Bug修复和性能优化")
    private String title;
    
    @Schema(description = "描述", example = "修复了多个已知问题，优化了系统性能")
    private String description;
    
    @Schema(description = "是否当前版本", example = "true")
    private Boolean isCurrentVersion;
    
    @Schema(description = "部署时间")
    private LocalDateTime deploymentDate;
    
    @Schema(description = "部署人", example = "系统管理员")
    private String deployedBy;
}
