package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 当前版本信息VO
 */
@Data
@Schema(description = "当前版本信息")
public class CurrentVersionVo {
    
    @Schema(description = "版本号", example = "1.5.2")
    private String version;
    
    @Schema(description = "构建号", example = "20251219001")
    private String buildNumber;
    
    @Schema(description = "发布日期", example = "2025-12-15")
    private String releaseDate;
    
    @Schema(description = "运行环境", example = "production")
    private String environment;
    
    @Schema(description = "组件信息")
    private Map<String, Object> components;
    
    @Schema(description = "运行时长", example = "15天 6小时 23分钟")
    private String uptime;
    
    @Schema(description = "启动时间", example = "2025-12-04 10:00:00")
    private LocalDateTime startTime;
}
