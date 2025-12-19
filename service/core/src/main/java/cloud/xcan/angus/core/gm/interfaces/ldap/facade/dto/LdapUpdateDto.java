package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import lombok.Data;

@Data
public class LdapUpdateDto {
    private Long id;
    private String name;
    private LdapStatus status;
    private String serverUrl;
    private String baseDn;
    private Boolean enabled;
    private String description;
}
