package cloud.xcan.angus.core.gm.domain.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 安全设置仓储接口
 */
public interface SecurityRepo extends JpaRepository<Security, Long> {

    /**
     * <p>Find by type</p>
     */
    List<Security> findByType(SecurityType type);
    
    /**
     * <p>Find by type with pagination</p>
     */
    Page<Security> findByType(SecurityType type, Pageable pageable);

    /**
     * 根据级别查找
     */
    List<Security> findByLevel(SecurityLevel level);

    /**
     * 根据范围查找
     */
    List<Security> findByScope(String scope);

    /**
     * 根据启用状态查找
     */
    Page<Security> findByEnabled(Boolean enabled, Pageable pageable);

    /**
     * 根据类型和范围查找
     */
    Optional<Security> findByTypeAndScope(SecurityType type, String scope);
}
