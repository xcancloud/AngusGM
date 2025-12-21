package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "敏感操作日志VO")
public class SensitiveLogVo implements Serializable {
    
    @Schema(description = "日志ID")
    private String id;
    
    @Schema(description = "用户ID")
    private String userId;
    
    @Schema(description = "用户名")
    private String userName;
    
    @Schema(description = "模块")
    private String module;
    
    @Schema(description = "操作类型")
    private String operation;
    
    @Schema(description = "操作描述")
    private String description;
    
    @Schema(description = "日志级别")
    private String level;
    
    @Schema(description = "操作时间")
    private LocalDateTime operationTime;
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "是否成功")
    private Boolean success;
}
