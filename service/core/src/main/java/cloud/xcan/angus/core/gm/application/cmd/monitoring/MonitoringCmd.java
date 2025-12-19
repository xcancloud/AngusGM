package cloud.xcan.angus.core.gm.application.cmd.monitoring;

import cloud.xcan.angus.core.gm.domain.monitoring.Monitoring;

public interface MonitoringCmd {
    Monitoring create(Monitoring monitoring);
    Monitoring update(Long id, Monitoring monitoring);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
}
