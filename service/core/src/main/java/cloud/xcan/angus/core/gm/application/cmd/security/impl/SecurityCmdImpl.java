package cloud.xcan.angus.core.gm.application.cmd.security.impl;

import cloud.xcan.angus.core.gm.application.cmd.security.SecurityCmd;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SecurityCmdImpl implements SecurityCmd {
    
    private final SecurityRepo securityRepo;
    
    @Override
    @Transactional
    public Security create(Security security) {
        return securityRepo.save(security);
    }
    
    @Override
    @Transactional
    public Security update(Long id, Security security) {
        Security existing = securityRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Security not found"));
        // Update fields
        return securityRepo.save(existing);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        securityRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        Security security = securityRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Security not found"));
        security.setEnabled(true);
        securityRepo.save(security);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        Security security = securityRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Security not found"));
        security.setEnabled(false);
        securityRepo.save(security);
    }
}
