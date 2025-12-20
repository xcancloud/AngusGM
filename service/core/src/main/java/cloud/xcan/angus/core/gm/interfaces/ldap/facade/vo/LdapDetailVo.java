package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.LdapType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LdapDetailVo {
    private Long id;
    private String name;
    private LdapType type;
    private LdapStatus status;
    private String serverUrl;
    private String baseDn;
    private Boolean syncEnabled;
    private Boolean enabled;
    private String description;
    private LocalDateTime createdAt;
}
