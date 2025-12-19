package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import cloud.xcan.angus.core.gm.domain.quota.QuotaStatus;
import cloud.xcan.angus.core.gm.domain.quota.QuotaType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuotaDetailVo {
    private Long id;
    private String name;
    private QuotaType type;
    private QuotaStatus status;
    private Long tenantId;
    private Long limitValue;
    private Long usedValue;
    private Integer warningThreshold;
    private String unit;
    private Boolean enabled;
    private String description;
    private LocalDateTime createdAt;
}
