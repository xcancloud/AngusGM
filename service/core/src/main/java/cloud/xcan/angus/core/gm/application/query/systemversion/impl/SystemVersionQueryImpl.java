package cloud.xcan.angus.core.gm.application.query.systemversion.impl;

import cloud.xcan.angus.core.gm.application.query.systemversion.SystemVersionQuery;
import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;
import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemVersionQueryImpl implements SystemVersionQuery {
    
    private final SystemVersionRepo systemVersionRepo;
    
    @Override
    public Optional<SystemVersion> findById(Long id) {
        return systemVersionRepo.findById(id);
    }
    
    @Override
    public Page<SystemVersion> findAll(Pageable pageable) {
        return systemVersionRepo.findAll(pageable);
    }
    
    @Override
    public long count() {
        return systemVersionRepo.count();
    }
}
