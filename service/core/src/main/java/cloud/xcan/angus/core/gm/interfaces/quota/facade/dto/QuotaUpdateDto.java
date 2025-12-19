package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import lombok.Data;

@Data
public class QuotaUpdateDto {
    private Long id;
    private String name;
    private QuotaStatus status;
    private Long limitValue;
    private Integer warningThreshold;
    private Boolean enabled;
    private String description;
}
