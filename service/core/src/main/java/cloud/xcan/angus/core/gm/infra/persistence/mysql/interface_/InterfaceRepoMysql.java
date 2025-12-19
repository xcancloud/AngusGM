package cloud.xcan.angus.core.gm.infra.persistence.mysql.interface_;

import cloud.xcan.angus.core.gm.domain.interface_.Interface;
import cloud.xcan.angus.core.gm.domain.interface_.InterfaceRepo;
import cloud.xcan.angus.core.gm.domain.interface_.enums.InterfaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterfaceRepoMysql extends JpaRepository<Interface, String>, InterfaceRepo {
    @Override
    default boolean existsByPath(String path) {
        return findByPath(path).isPresent();
    }
    
    java.util.Optional<Interface> findByPath(String path);
    
    @Override
    default long countByStatus(InterfaceStatus status) {
        return count();
    }
}
