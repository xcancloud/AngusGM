package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "LDAP同步历史VO")
public class LdapSyncHistoryVo implements Serializable {
    
    @Schema(description = "同步记录ID")
    private String id;
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "耗时")
    private String duration;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "总用户数")
    private Integer totalUsers;
    
    @Schema(description = "新增用户数")
    private Integer newUsers;
    
    @Schema(description = "更新用户数")
    private Integer updatedUsers;
    
    @Schema(description = "删除用户数")
    private Integer deletedUsers;
    
    @Schema(description = "失败用户数")
    private Integer failedUsers;
    
    @Schema(description = "同步类型（auto/manual）")
    private String syncType;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
}
