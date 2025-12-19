package cloud.xcan.angus.core.gm.domain.quota;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 配额实体
 * 资源配额管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_quota")
public class Quota extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private QuotaType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private QuotaStatus status;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "limit_value", nullable = false)
    private Long limitValue;

    @Column(name = "used_value", nullable = false)
    private Long usedValue = 0L;

    @Column(name = "warning_threshold")
    private Integer warningThreshold;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "description", length = 500)
    private String description;
}
