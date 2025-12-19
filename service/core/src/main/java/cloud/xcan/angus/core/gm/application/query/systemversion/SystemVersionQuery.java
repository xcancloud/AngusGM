package cloud.xcan.angus.core.gm.application.query.systemversion;

import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SystemVersionQuery {
    Optional<SystemVersion> findById(Long id);
    Page<SystemVersion> findAll(Pageable pageable);
    long count();
}
