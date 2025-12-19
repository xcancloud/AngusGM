package cloud.xcan.angus.core.gm.application.query.apimonitoring.impl;

import cloud.xcan.angus.core.gm.application.query.apimonitoring.ApiMonitoringQuery;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;
import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoringRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiMonitoringQueryImpl implements ApiMonitoringQuery {
    
    private final ApiMonitoringRepo apiMonitoringRepo;
    
    @Override
    public Optional<ApiMonitoring> findById(Long id) {
        return apiMonitoringRepo.findById(id);
    }
    
    @Override
    public Page<ApiMonitoring> findAll(Pageable pageable) {
        return apiMonitoringRepo.findAll(pageable);
    }
    
    @Override
    public long count() {
        return apiMonitoringRepo.count();
    }
}
