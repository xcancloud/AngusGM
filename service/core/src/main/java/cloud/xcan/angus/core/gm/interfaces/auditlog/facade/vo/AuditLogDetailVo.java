package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "审计日志详情VO")
public class AuditLogDetailVo implements Serializable {
    
    @Schema(description = "日志ID")
    private String id;
    
    @Schema(description = "用户ID")
    private String userId;
    
    @Schema(description = "用户名")
    private String userName;
    
    @Schema(description = "用户头像")
    private String userAvatar;
    
    @Schema(description = "用户部门")
    private String userDepartment;
    
    @Schema(description = "模块")
    private String module;
    
    @Schema(description = "模块名称")
    private String moduleName;
    
    @Schema(description = "操作类型")
    private String operation;
    
    @Schema(description = "操作名称")
    private String operationName;
    
    @Schema(description = "操作描述")
    private String description;
    
    @Schema(description = "日志级别")
    private String level;
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "地理位置")
    private String location;
    
    @Schema(description = "设备信息")
    private String device;
    
    @Schema(description = "User Agent")
    private String userAgent;
    
    @Schema(description = "操作时间")
    private LocalDateTime operationTime;
    
    @Schema(description = "耗时（毫秒）")
    private Integer duration;
    
    @Schema(description = "是否成功")
    private Boolean success;
    
    @Schema(description = "请求URL")
    private String requestUrl;
    
    @Schema(description = "请求方法")
    private String requestMethod;
    
    @Schema(description = "请求头")
    private Map<String, String> requestHeaders;
    
    @Schema(description = "请求数据")
    private Map<String, Object> requestData;
    
    @Schema(description = "响应状态码")
    private Integer responseStatus;
    
    @Schema(description = "响应数据")
    private Map<String, Object> responseData;
    
    @Schema(description = "变更记录")
    private List<Change> changes;
    
    @Data
    @Schema(description = "变更记录")
    public static class Change implements Serializable {
        @Schema(description = "字段")
        private String field;
        
        @Schema(description = "旧值")
        private Object oldValue;
        
        @Schema(description = "新值")
        private Object newValue;
    }
}
