package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "错误率TOP接口VO")
public class TopErrorsVo implements Serializable {
    
    @Schema(description = "服务名称")
    private String serviceName;
    
    @Schema(description = "接口路径")
    private String path;
    
    @Schema(description = "请求方法")
    private String method;
    
    @Schema(description = "错误率")
    private Double errorRate;
    
    @Schema(description = "调用次数")
    private Long calls;
    
    @Schema(description = "失败次数")
    private Long failedCalls;
}
