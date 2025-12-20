package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.LdapType;
import lombok.Data;

@Data
public class LdapListVo {
    private Long id;
    private String name;
    private LdapType type;
    private LdapStatus status;
    private Boolean enabled;
}
