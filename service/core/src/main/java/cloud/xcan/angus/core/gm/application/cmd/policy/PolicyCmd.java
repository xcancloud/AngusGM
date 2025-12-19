package cloud.xcan.angus.core.gm.application.cmd.policy;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;

public interface PolicyCmd {
    String create(PolicyCreateDto dto);
    void update(String id, PolicyUpdateDto dto);
    void enable(String id);
    void disable(String id);
    void delete(String id);
}
