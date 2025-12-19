package cloud.xcan.angus.core.gm.infra.persistence.mysql.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LdapRepoMysql extends LdapRepo, JpaRepository<Ldap, Long> {
}
