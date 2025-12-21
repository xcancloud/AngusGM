package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建告警规则DTO")
public class AlertRuleCreateDto {
    
    @NotBlank
    @Schema(description = "规则名称", required = true, example = "内存使用率告警")
    private String name;
    
    @NotBlank
    @Schema(description = "监控指标", required = true, example = "memory_usage")
    private String metric;
    
    @NotBlank
    @Schema(description = "条件", required = true, example = ">")
    private String condition;
    
    @NotNull
    @Schema(description = "阈值", required = true, example = "90")
    private Double threshold;
    
    @Schema(description = "持续时间（秒）", example = "300")
    private Integer duration;
    
    @NotBlank
    @Schema(description = "告警等级", required = true, example = "高")
    private String level;
    
    @Schema(description = "通知渠道")
    private List<String> notifyChannels;
}
