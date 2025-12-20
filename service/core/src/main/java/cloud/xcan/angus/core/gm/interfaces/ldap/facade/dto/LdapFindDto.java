package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.LdapType;
import lombok.Data;

@Data
public class LdapFindDto {
    private String name;
    private LdapType type;
    private LdapStatus status;
}
