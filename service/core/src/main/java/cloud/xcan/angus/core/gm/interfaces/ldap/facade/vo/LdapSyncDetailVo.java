package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "LDAP同步详情VO")
public class LdapSyncDetailVo implements Serializable {
    
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
    
    @Schema(description = "详细信息")
    private SyncDetails details;
    
    @Data
    @Schema(description = "同步详细信息")
    public static class SyncDetails implements Serializable {
        @Schema(description = "新增用户列表")
        private List<UserInfo> newUsersList;
        
        @Schema(description = "更新用户列表")
        private List<UpdatedUserInfo> updatedUsersList;
        
        @Schema(description = "删除用户列表")
        private List<UserInfo> deletedUsersList;
        
        @Schema(description = "失败用户列表")
        private List<UserInfo> failedUsersList;
    }
    
    @Data
    @Schema(description = "用户信息")
    public static class UserInfo implements Serializable {
        @Schema(description = "用户名")
        private String username;
        
        @Schema(description = "姓名")
        private String name;
        
        @Schema(description = "邮箱")
        private String email;
    }
    
    @Data
    @Schema(description = "更新用户信息")
    public static class UpdatedUserInfo implements Serializable {
        @Schema(description = "用户名")
        private String username;
        
        @Schema(description = "姓名")
        private String name;
        
        @Schema(description = "变更字段")
        private List<String> changes;
    }
}
