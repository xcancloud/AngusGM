package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailSmtpVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String fromName;
    private String fromEmail;
    private Boolean useSsl;
    private Boolean isDefault;
}
