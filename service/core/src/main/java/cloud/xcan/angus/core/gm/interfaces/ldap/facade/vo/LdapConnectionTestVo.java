package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "LDAP连接测试结果VO")
public class LdapConnectionTestVo implements Serializable {
    
    @Schema(description = "是否连接成功")
    private Boolean connected;
    
    @Schema(description = "测试时间")
    private LocalDateTime testTime;
    
    @Schema(description = "响应时间（毫秒）")
    private Integer responseTime;
    
    @Schema(description = "服务器信息")
    private ServerInfo serverInfo;
    
    @Data
    @Schema(description = "服务器信息")
    public static class ServerInfo implements Serializable {
        @Schema(description = "LDAP版本")
        private String version;
        
        @Schema(description = "厂商")
        private String vendor;
    }
}
