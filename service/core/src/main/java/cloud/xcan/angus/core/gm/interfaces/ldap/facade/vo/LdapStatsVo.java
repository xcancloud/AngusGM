package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import lombok.Data;

@Data
public class LdapStatsVo {
    private Long totalCount;
    private Long connectedCount;
    private Long enabledCount;
}
