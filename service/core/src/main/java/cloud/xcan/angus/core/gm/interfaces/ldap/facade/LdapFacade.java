package cloud.xcan.angus.core.gm.interfaces.ldap.facade;

import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.*;

import java.util.List;

public interface LdapFacade {
    LdapDetailVo create(LdapCreateDto dto);
    LdapDetailVo update(LdapUpdateDto dto);
    void delete(Long id);
    LdapDetailVo findById(Long id);
    List<LdapListVo> findAll(LdapFindDto dto);
    LdapStatsVo getStats();
}
