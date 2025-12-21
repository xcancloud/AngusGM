package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TwoFactorConfigVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private Boolean isEnabled;
    private List<String> methods;
    private String defaultMethod;
    private Integer codeExpiration;
    private Integer codeLength;
    private Integer trustDeviceDays;
    private Boolean enforceForAdmins;
    private Boolean enforceForAllUsers;
}
