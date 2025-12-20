package cloud.xcan.angus.core.gm.infra.persistence.mysql.security;

import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityRepoMysql extends JpaRepository<Security, Long>, SecurityRepo {
}
