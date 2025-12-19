package cloud.xcan.angus.core.gm.application.query.security.impl;

import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecurityQueryImpl implements SecurityQuery {
    
    private final SecurityRepo securityRepo;
    
    @Override
    public Optional<Security> findById(Long id) {
        return securityRepo.findById(id);
    }
    
    @Override
    public Page<Security> findAll(Pageable pageable) {
        return securityRepo.findAll(pageable);
    }
    
    @Override
    public long count() {
        return securityRepo.count();
    }
}
