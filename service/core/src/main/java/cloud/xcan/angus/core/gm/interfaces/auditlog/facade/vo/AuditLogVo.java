package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "审计日志VO")
public class AuditLogVo extends TenantAuditingVo {
    
    @Schema(description = "日志ID")
    private String id;
    
    @Schema(description = "用户ID")
    private String userId;
    
    @Schema(description = "用户名")
    private String userName;
    
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
    
    @Schema(description = "操作时间")
    private LocalDateTime operationTime;
    
    @Schema(description = "耗时（毫秒）")
    private Integer duration;
    
    @Schema(description = "是否成功")
    private Boolean success;
}
