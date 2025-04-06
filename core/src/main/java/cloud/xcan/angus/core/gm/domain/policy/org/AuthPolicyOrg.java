package cloud.xcan.angus.core.gm.domain.policy.org;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Internal non multi tenant table.
 *
 * @author XiaoLong Liu
 */
@Setter
@Getter
@Accessors(chain = true)
@Entity
@Table(name = "auth_policy_org")
@EntityListeners({AuditingEntityListener.class})
public class AuthPolicyOrg extends EntitySupport<AuthPolicyOrg, Long> {

  @Id
  private Long id;

  @Column(name = "policy_id")
  private Long policyId;

  @Column(name = "policy_type")
  @Enumerated(EnumType.STRING)
  private PolicyType policyType;

  @Column(name = "org_type")
  @Enumerated(EnumType.STRING)
  private AuthOrgType orgType;

  @Column(name = "org_id")
  private Long orgId;

  @Column(name = "app_id")
  private Long appId;

  @Enumerated(EnumType.STRING)
  @Column(name = "grant_scope")
  private PolicyGrantScope grantScope;

  @Column(name = "open_auth")
  private Boolean openAuth;

  @Column(name = "tenant_id")
  protected Long tenantId;

  /**
   * Tenant user default policy.
   */
  private Boolean default0;

  @CreatedBy
  @Column(name = "created_by")
  private Long createdBy;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @Transient
  private App app;
  @Transient
  private List<AuthPolicy> defaultPolices;

  @Override
  public boolean sameIdentityAs(AuthPolicyOrg other) {
    if (isNull(other)) {
      return false;
    }
    // Unique index
    return appId.equals(other.appId) && policyId.equals(other.policyId) && orgId
        .equals(other.orgId) && openAuth.equals(other.openAuth) && default0.equals(other.default0);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
