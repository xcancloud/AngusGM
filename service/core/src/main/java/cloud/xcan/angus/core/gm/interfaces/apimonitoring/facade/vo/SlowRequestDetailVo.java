package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "慢请求详情VO")
public class SlowRequestDetailVo implements Serializable {
    
    @Schema(description = "记录ID")
    private String id;
    
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
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "用户ID")
    private String userId;
    
    @Schema(description = "用户名")
    private String userName;
    
    @Schema(description = "请求头")
    private Map<String, String> requestHeaders;
    
    @Schema(description = "请求体")
    private String requestBody;
    
    @Schema(description = "响应体")
    private String responseBody;
    
    @Schema(description = "耗时分解")
    private Map<String, Integer> breakdown;
    
    @Schema(description = "SQL语句列表")
    private List<SqlStatement> sqlStatements;
    
    @Data
    @Schema(description = "SQL语句")
    public static class SqlStatement implements Serializable {
        @Schema(description = "SQL语句")
        private String sql;
        
        @Schema(description = "耗时（毫秒）")
        private Integer duration;
        
        @Schema(description = "影响行数")
        private Integer rows;
    }
}
