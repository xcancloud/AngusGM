package cloud.xcan.angus.core.gm.application.query.security;

import cloud.xcan.angus.core.gm.domain.security.Security;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SecurityQuery {
    Optional<Security> findById(Long id);
    Page<Security> findAll(Pageable pageable);
    long count();
}
