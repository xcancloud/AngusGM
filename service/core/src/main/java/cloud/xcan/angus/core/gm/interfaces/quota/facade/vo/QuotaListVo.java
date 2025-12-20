package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import cloud.xcan.angus.core.gm.domain.quota.QuotaType;
import lombok.Data;

@Data
public class QuotaListVo {
    private Long id;
    private String name;
    private QuotaType type;
    private QuotaStatus status;
    private Long limitValue;
    private Long usedValue;
}
