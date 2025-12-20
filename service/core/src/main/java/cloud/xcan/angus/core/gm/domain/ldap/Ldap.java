package cloud.xcan.angus.core.gm.domain.ldap;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * LDAP实体
 * LDAP集成配置
 */
@Getter
@Setter
@Entity
@Table(name = "gm_ldap")
public class Ldap extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private LdapType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private LdapStatus status;

    @Column(name = "server_url", length = 500, nullable = false)
    private String serverUrl;

    @Column(name = "base_dn", length = 500, nullable = false)
    private String baseDn;

    @Column(name = "bind_dn", length = 500)
    private String bindDn;

    @Column(name = "bind_password", length = 500)
    private String bindPassword;

    @Column(name = "user_filter", length = 500)
    private String userFilter;

    @Column(name = "group_filter", length = 500)
    private String groupFilter;

    @Column(name = "sync_enabled", nullable = false)
    private Boolean syncEnabled = false;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "description", length = 500)
    private String description;
}
