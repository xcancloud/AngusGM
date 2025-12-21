package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "慢请求查询DTO")
public class SlowRequestFindDto implements Serializable {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "20")
    private Integer size = 20;
    
    @Schema(description = "服务名称筛选")
    private String serviceName;
    
    @Schema(description = "接口路径筛选")
    private String path;
    
    @Schema(description = "最小耗时（毫秒）", example = "1000")
    private Integer minDuration = 1000;
    
    @Schema(description = "开始日期")
    private String startDate;
    
    @Schema(description = "结束日期")
    private String endDate;
}
