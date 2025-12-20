package cloud.xcan.angus.core.gm.domain.ldap;

import cloud.xcan.angus.core.gm.domain.BaseRepo;

import java.util.List;
import java.util.Optional;

/**
 * LDAP仓储接口
 */
public interface LdapRepo extends BaseRepo<Ldap> {
    
    Optional<Ldap> findByName(String name);
    
    List<Ldap> findByType(LdapType type);
    
    List<Ldap> findByStatus(LdapStatus status);
    
    List<Ldap> findByEnabled(Boolean enabled);
    
    long countByType(LdapType type);
}
