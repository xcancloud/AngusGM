package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新检查结果VO
 */
@Data
@Schema(description = "更新检查结果")
public class UpdateCheckVo {
    
    @Schema(description = "是否有更新", example = "true")
    private Boolean hasUpdate;
    
    @Schema(description = "当前版本", example = "1.5.2")
    private String currentVersion;
    
    @Schema(description = "最新版本", example = "1.6.0")
    private String latestVersion;
    
    @Schema(description = "发布日期", example = "2025-12-20")
    private String releaseDate;
    
    @Schema(description = "发布类型", example = "minor")
    private String releaseType;
    
    @Schema(description = "标题", example = "新增资源配额管理功能")
    private String title;
    
    @Schema(description = "描述", example = "支持租户资源配额的精细化管理和监控")
    private String description;
    
    @Schema(description = "下载地址", example = "https://releases.angusgm.com/v1.6.0/angusgm-1.6.0.tar.gz")
    private String downloadUrl;
    
    @Schema(description = "文件大小", example = "125 MB")
    private String fileSize;
    
    @Schema(description = "发布说明地址", example = "https://docs.angusgm.com/releases/v1.6.0")
    private String releaseNotes;
    
    @Schema(description = "是否有破坏性变更", example = "false")
    private Boolean breakingChanges;
    
    @Schema(description = "是否需要停机", example = "false")
    private Boolean requiresDowntime;
}
