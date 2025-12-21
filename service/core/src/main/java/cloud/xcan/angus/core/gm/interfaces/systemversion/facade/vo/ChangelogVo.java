package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 变更日志VO
 */
@Data
@Schema(description = "变更日志")
public class ChangelogVo {
    
    @Schema(description = "版本号", example = "1.5.2")
    private String version;
    
    @Schema(description = "发布日期", example = "2025-12-15")
    private String releaseDate;
    
    @Schema(description = "变更列表")
    private List<ChangeItem> changes;
    
    /**
     * 变更项
     */
    @Data
    @Schema(description = "变更项")
    public static class ChangeItem {
        @Schema(description = "变更类型", example = "fix")
        private String type;
        
        @Schema(description = "模块", example = "用户管理")
        private String module;
        
        @Schema(description = "描述", example = "修复了批量导入用户时的数据校验问题")
        private String description;
        
        @Schema(description = "关联问题ID", example = "ISSUE-1234")
        private String issueId;
    }
}
