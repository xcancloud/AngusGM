package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * <p>SMS provider domain entity</p>
 */
@Getter
@Setter
@Entity
@Table(name = "gm_sms_provider")
public class SmsProvider extends TenantAuditingEntity<SmsProvider, Long> {

    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Type(JsonType.class)
    @Column(name = "config", columnDefinition = "json")
    private Map<String, String> config;

    @Transient
    private Long balance;

    @Transient
    private Long monthlyQuota;

    @Override
    public Long identity() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SmsProvider provider)) return false;
        return Objects.equals(id, provider.id)
                && Objects.equals(code, provider.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}

