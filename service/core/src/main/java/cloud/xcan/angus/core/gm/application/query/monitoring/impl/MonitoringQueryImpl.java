package cloud.xcan.angus.core.gm.application.query.monitoring.impl;

import cloud.xcan.angus.core.gm.application.query.monitoring.MonitoringQuery;
import cloud.xcan.angus.core.gm.domain.monitoring.Monitoring;
import cloud.xcan.angus.core.gm.domain.monitoring.MonitoringRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringQueryImpl implements MonitoringQuery {
    
    private final MonitoringRepo monitoringRepo;
    
    @Override
    public Optional<Monitoring> findById(Long id) {
        return monitoringRepo.findById(id);
    }
    
    @Override
    public Page<Monitoring> findAll(Pageable pageable) {
        return monitoringRepo.findAll(pageable);
    }
    
    @Override
    public long count() {
        return monitoringRepo.count();
    }
}
