package cloud.xcan.angus.core.gm.domain.monitoring;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 监控实体
 * 系统监控管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_monitoring")
public class Monitoring extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private MonitoringType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private MonitoringStatus status;

    @Column(name = "target", length = 200, nullable = false)
    private String target;

    @Column(name = "metrics", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object metrics;

    @Column(name = "threshold_config", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object thresholdConfig;

    @Column(name = "alert_enabled", nullable = false)
    private Boolean alertEnabled = true;

    @Column(name = "check_interval")
    private Integer checkInterval;

    @Column(name = "last_check_time")
    private LocalDateTime lastCheckTime;

    @Column(name = "description", length = 500)
    private String description;
}
