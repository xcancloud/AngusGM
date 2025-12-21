package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 系统依赖信息VO
 */
@Data
@Schema(description = "系统依赖信息")
public class DependenciesVo {
    
    @Schema(description = "后端依赖")
    private List<DependencyItem> backend;
    
    @Schema(description = "前端依赖")
    private List<DependencyItem> frontend;
    
    @Schema(description = "基础设施")
    private List<DependencyItem> infrastructure;
    
    /**
     * 依赖项
     */
    @Data
    @Schema(description = "依赖项")
    public static class DependencyItem {
        @Schema(description = "名称", example = "Spring Boot")
        private String name;
        
        @Schema(description = "版本", example = "3.2.0")
        private String version;
        
        @Schema(description = "类型", example = "framework")
        private String type;
    }
}
