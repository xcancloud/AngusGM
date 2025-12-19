package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import cloud.xcan.angus.core.gm.domain.ldap.LdapType;
import lombok.Data;

@Data
public class LdapCreateDto {
    private String name;
    private LdapType type;
    private String serverUrl;
    private String baseDn;
    private String bindDn;
    private String bindPassword;
    private String userFilter;
    private String groupFilter;
    private Boolean syncEnabled;
    private String description;
}
