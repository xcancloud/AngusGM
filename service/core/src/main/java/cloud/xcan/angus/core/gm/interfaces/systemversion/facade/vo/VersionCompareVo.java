package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 版本对比VO
 */
@Data
@Schema(description = "版本对比结果")
public class VersionCompareVo {
    
    @Schema(description = "起始版本", example = "1.5.0")
    private String fromVersion;
    
    @Schema(description = "目标版本", example = "1.5.2")
    private String toVersion;
    
    @Schema(description = "功能变更")
    private FeatureChanges features;
    
    @Schema(description = "破坏性变更")
    private List<String> breakingChanges;
    
    @Schema(description = "迁移信息")
    private List<String> migrations;
    
    /**
     * 功能变更
     */
    @Data
    @Schema(description = "功能变更")
    public static class FeatureChanges {
        @Schema(description = "新增功能")
        private List<FeatureItem> added;
        
        @Schema(description = "改进功能")
        private List<FeatureItem> improved;
        
        @Schema(description = "修复问题")
        private List<FeatureItem> fixed;
        
        @Schema(description = "移除功能")
        private List<FeatureItem> removed;
    }
    
    /**
     * 功能项
     */
    @Data
    @Schema(description = "功能项")
    public static class FeatureItem {
        @Schema(description = "模块", example = "系统监控")
        private String module;
        
        @Schema(description = "描述", example = "新增CPU使用率告警功能")
        private String description;
    }
}
