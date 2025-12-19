package cloud.xcan.angus.core.gm.application.cmd.apimonitoring.impl;

import cloud.xcan.angus.core.gm.application.cmd.apimonitoring.ApiMonitoringCmd;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiMonitoringCmdImpl implements ApiMonitoringCmd {
    
    private final ApiMonitoringRepo apiMonitoringRepo;
    
    @Override
    @Transactional
    public ApiMonitoring create(ApiMonitoring apiMonitoring) {
        return apiMonitoringRepo.save(apiMonitoring);
    }
    
    @Override
    @Transactional
    public ApiMonitoring update(Long id, ApiMonitoring apiMonitoring) {
        ApiMonitoring existing = apiMonitoringRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("ApiMonitoring not found"));
        // Update fields
        return apiMonitoringRepo.save(existing);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        apiMonitoringRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        ApiMonitoring apiMonitoring = apiMonitoringRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("ApiMonitoring not found"));
        apiMonitoring.setEnabled(true);
        apiMonitoringRepo.save(apiMonitoring);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        ApiMonitoring apiMonitoring = apiMonitoringRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("ApiMonitoring not found"));
        apiMonitoring.setEnabled(false);
        apiMonitoringRepo.save(apiMonitoring);
    }
}
