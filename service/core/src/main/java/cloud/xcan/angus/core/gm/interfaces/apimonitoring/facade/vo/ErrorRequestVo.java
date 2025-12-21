package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "错误请求VO")
public class ErrorRequestVo extends TenantAuditingVo implements Serializable {
    
    @Schema(description = "记录ID")
    private Long id;
    
    @Schema(description = "链路追踪ID")
    private String traceId;
    
    @Schema(description = "服务名称")
    private String serviceName;
    
    @Schema(description = "接口路径")
    private String path;
    
    @Schema(description = "请求方法")
    private String method;
    
    @Schema(description = "请求时间")
    private LocalDateTime requestTime;
    
    @Schema(description = "耗时（毫秒）")
    private Integer duration;
    
    @Schema(description = "HTTP状态码")
    private Integer statusCode;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @Schema(description = "错误类型")
    private String errorType;
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "用户ID")
    private String userId;
}
