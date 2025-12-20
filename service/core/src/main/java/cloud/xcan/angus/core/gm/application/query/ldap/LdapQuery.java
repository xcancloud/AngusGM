package cloud.xcan.angus.core.gm.application.query.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.LdapType;

import java.util.List;
import java.util.Optional;

public interface LdapQuery {
    Optional<Ldap> findById(Long id);
    Optional<Ldap> findByName(String name);
    List<Ldap> findByType(LdapType type);
    List<Ldap> findByStatus(LdapStatus status);
    List<Ldap> findAll();
}
