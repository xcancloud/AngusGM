package cloud.xcan.angus.core.gm.domain.security;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 安全设置实体
 * 系统安全配置管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_security")
public class Security extends BaseEntity {

    @Id
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private SecurityType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 20, nullable = false)
    private SecurityLevel level;

    @Column(name = "scope", length = 20, nullable = false)
    private String scope;

    @Column(name = "config", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object config;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "version", nullable = false)
    private Integer version = 1;
}
