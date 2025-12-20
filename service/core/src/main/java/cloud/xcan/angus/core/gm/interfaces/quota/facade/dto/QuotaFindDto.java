package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import cloud.xcan.angus.core.gm.domain.quota.QuotaType;
import lombok.Data;

@Data
public class QuotaFindDto {
    private String name;
    private QuotaType type;
    private QuotaStatus status;
    private Long tenantId;
}
