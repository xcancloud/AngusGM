package cloud.xcan.angus.core.gm.application.cmd.monitoring.impl;

import cloud.xcan.angus.core.gm.application.cmd.monitoring.MonitoringCmd;
import cloud.xcan.angus.core.gm.domain.monitoring.Monitoring;
import cloud.xcan.angus.core.gm.domain.monitoring.MonitoringRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitoringCmdImpl implements MonitoringCmd {
    
    private final MonitoringRepo monitoringRepo;
    
    @Override
    @Transactional
    public Monitoring create(Monitoring monitoring) {
        return monitoringRepo.save(monitoring);
    }
    
    @Override
    @Transactional
    public Monitoring update(Long id, Monitoring monitoring) {
        Monitoring existing = monitoringRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Monitoring not found"));
        // Update fields
        return monitoringRepo.save(existing);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        monitoringRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        Monitoring monitoring = monitoringRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Monitoring not found"));
        monitoring.setEnabled(true);
        monitoringRepo.save(monitoring);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        Monitoring monitoring = monitoringRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Monitoring not found"));
        monitoring.setEnabled(false);
        monitoringRepo.save(monitoring);
    }
}
