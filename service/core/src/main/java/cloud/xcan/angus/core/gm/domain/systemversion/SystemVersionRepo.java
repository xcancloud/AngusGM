package cloud.xcan.angus.core.gm.domain.systemversion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 系统版本仓储接口
 */
public interface SystemVersionRepo extends JpaRepository<SystemVersion, Long> {

    /**
     * 根据版本号查找
     */
    Optional<SystemVersion> findByVersionNumber(String versionNumber);

    /**
     * 根据类型查找
     */
    List<SystemVersion> findByType(VersionType type);

    /**
     * 根据状态查找
     */
    Page<SystemVersion> findByStatus(VersionStatus status, Pageable pageable);

    /**
     * 根据类型和状态查找
     */
    List<SystemVersion> findByTypeAndStatus(VersionType type, VersionStatus status);

    /**
     * 查找最新版本
     */
    Optional<SystemVersion> findTopByStatusOrderByReleaseDateDesc(VersionStatus status);
}
