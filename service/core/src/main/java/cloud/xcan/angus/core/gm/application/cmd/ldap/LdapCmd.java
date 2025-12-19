package cloud.xcan.angus.core.gm.application.cmd.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.Ldap;

public interface LdapCmd {
    Ldap create(Ldap ldap);
    Ldap update(Ldap ldap);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
    void testConnection(Long id);
    void syncUsers(Long id);
}
