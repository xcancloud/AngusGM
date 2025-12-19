package cloud.xcan.angus.core.gm.application.cmd.systemversion;

import cloud.xcan.angus.core.gm.domain.systemversion.SystemVersion;

public interface SystemVersionCmd {
    SystemVersion create(SystemVersion systemVersion);
    SystemVersion update(Long id, SystemVersion systemVersion);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
}
