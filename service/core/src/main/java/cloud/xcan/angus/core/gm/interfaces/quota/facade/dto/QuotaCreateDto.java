package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import cloud.xcan.angus.core.gm.domain.quota.QuotaType;
import lombok.Data;

@Data
public class QuotaCreateDto {
    private String name;
    private QuotaType type;
    private Long tenantId;
    private Long limitValue;
    private Integer warningThreshold;
    private String unit;
    private String description;
}
