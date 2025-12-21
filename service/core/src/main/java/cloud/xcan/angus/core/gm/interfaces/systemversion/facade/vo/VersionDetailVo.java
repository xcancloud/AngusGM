package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 版本详情VO
 */
@Data
@Schema(description = "版本详情")
public class VersionDetailVo {
    
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
    
    @Schema(description = "特性列表")
    private List<FeatureItem> features;
    
    @Schema(description = "破坏性变更")
    private List<String> breakingChanges;
    
    @Schema(description = "迁移信息")
    private List<String> migrations;
    
    @Schema(description = "依赖变更")
    private Map<String, Object> dependencies;
    
    /**
     * 特性项
     */
    @Data
    @Schema(description = "特性项")
    public static class FeatureItem {
        @Schema(description = "类型", example = "fix")
        private String type;
        
        @Schema(description = "模块", example = "用户管理")
        private String module;
        
        @Schema(description = "描述", example = "修复了批量导入用户时的数据校验问题")
        private String description;
    }
}
