package cloud.xcan.angus.core.gm.application.cmd.apimonitoring;

import cloud.xcan.angus.core.gm.domain.apimonitoring.ApiMonitoring;

public interface ApiMonitoringCmd {
    ApiMonitoring create(ApiMonitoring apiMonitoring);
    ApiMonitoring update(Long id, ApiMonitoring apiMonitoring);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
}
