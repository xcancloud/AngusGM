package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "接口统计VO")
public class InterfaceStatsVo implements Serializable {
    
    @Schema(description = "统计ID")
    private Long id;
    
    @Schema(description = "服务名称")
    private String serviceName;
    
    @Schema(description = "接口路径")
    private String path;
    
    @Schema(description = "请求方法")
    private String method;
    
    @Schema(description = "总调用次数")
    private Long totalCalls;
    
    @Schema(description = "成功调用次数")
    private Long successCalls;
    
    @Schema(description = "失败调用次数")
    private Long failedCalls;
    
    @Schema(description = "平均响应时间（毫秒）")
    private Integer avgResponseTime;
    
    @Schema(description = "最大响应时间（毫秒）")
    private Integer maxResponseTime;
    
    @Schema(description = "最小响应时间（毫秒）")
    private Integer minResponseTime;
    
    @Schema(description = "错误率（%）")
    private Double errorRate;
    
    @Schema(description = "QPS")
    private Integer qps;
}
