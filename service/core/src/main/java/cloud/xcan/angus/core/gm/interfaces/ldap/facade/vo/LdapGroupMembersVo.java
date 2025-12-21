package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "LDAP组成员VO")
public class LdapGroupMembersVo implements Serializable {
    
    @Schema(description = "组DN")
    private String groupDN;
    
    @Schema(description = "组名称")
    private String groupName;
    
    @Schema(description = "成员列表")
    private List<Member> members;
    
    @Data
    @Schema(description = "成员信息")
    public static class Member implements Serializable {
        @Schema(description = "用户DN")
        private String dn;
        
        @Schema(description = "用户ID")
        private String uid;
        
        @Schema(description = "通用名称")
        private String cn;
        
        @Schema(description = "邮箱")
        private String mail;
    }
}
