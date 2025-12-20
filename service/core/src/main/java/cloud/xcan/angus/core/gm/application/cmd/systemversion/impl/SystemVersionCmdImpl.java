package cloud.xcan.angus.core.gm.application.cmd.systemversion.impl;

import cloud.xcan.angus.core.gm.application.cmd.systemversion.SystemVersionCmd;
import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;
import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemVersionCmdImpl implements SystemVersionCmd {
    
    private final SystemVersionRepo systemVersionRepo;
    
    @Override
    @Transactional
    public SystemVersion create(SystemVersion systemVersion) {
        return systemVersionRepo.save(systemVersion);
    }
    
    @Override
    @Transactional
    public SystemVersion update(Long id, SystemVersion systemVersion) {
        SystemVersion existing = systemVersionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("SystemVersion not found"));
        // Update fields
        return systemVersionRepo.save(existing);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        systemVersionRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        SystemVersion systemVersion = systemVersionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("SystemVersion not found"));
        systemVersion.setEnabled(true);
        systemVersionRepo.save(systemVersion);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        SystemVersion systemVersion = systemVersionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("SystemVersion not found"));
        systemVersion.setEnabled(false);
        systemVersionRepo.save(systemVersion);
    }
}
