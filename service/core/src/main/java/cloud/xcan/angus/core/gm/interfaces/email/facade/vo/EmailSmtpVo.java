package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "SMTP配置VO")
public class EmailSmtpVo extends TenantAuditingVo {
    
    @Schema(description = "SMTP配置ID")
    private Long id;
    
    @Schema(description = "SMTP服务器地址")
    private String host;
    
    @Schema(description = "SMTP端口")
    private Integer port;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "发件人名称")
    private String fromName;
    
    @Schema(description = "发件人邮箱")
    private String fromEmail;
    
    @Schema(description = "是否使用SSL")
    private Boolean useSsl;
    
    @Schema(description = "是否默认配置")
    private Boolean isDefault;
}
