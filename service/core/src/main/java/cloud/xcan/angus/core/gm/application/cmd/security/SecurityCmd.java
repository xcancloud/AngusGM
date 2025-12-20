package cloud.xcan.angus.core.gm.application.cmd.security;

import cloud.xcan.angus.core.gm.domain.security.Security;

public interface SecurityCmd {
    Security create(Security security);
    Security update(Long id, Security security);
    void delete(Long id);
    void enable(Long id);
    void disable(Long id);
}
