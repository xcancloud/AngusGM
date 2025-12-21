package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "接口监控概览VO")
public class InterfaceMonitoringOverviewVo implements Serializable {
    
    @Schema(description = "总请求数")
    private Long totalRequests;
    
    @Schema(description = "成功请求数")
    private Long successRequests;
    
    @Schema(description = "失败请求数")
    private Long failedRequests;
    
    @Schema(description = "平均响应时间（毫秒）")
    private Integer avgResponseTime;
    
    @Schema(description = "当前QPS")
    private Integer qps;
    
    @Schema(description = "错误率（%）")
    private Double errorRate;
    
    @Schema(description = "慢请求数量")
    private Integer slowRequestCount;
}
