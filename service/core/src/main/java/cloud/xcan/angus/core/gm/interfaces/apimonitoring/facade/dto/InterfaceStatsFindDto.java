package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "接口统计查询DTO")
public class InterfaceStatsFindDto implements Serializable {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "20")
    private Integer size = 20;
    
    @Schema(description = "服务名称筛选")
    private String serviceName;
    
    @Schema(description = "接口路径筛选")
    private String path;
    
    @Schema(description = "请求方法筛选")
    private String method;
    
    @Schema(description = "开始日期")
    private String startDate;
    
    @Schema(description = "结束日期")
    private String endDate;
    
    @Schema(description = "排序字段（calls、avgTime、errorRate）")
    private String sortBy;
    
    @Schema(description = "排序方向（asc、desc）")
    private String order;
}
