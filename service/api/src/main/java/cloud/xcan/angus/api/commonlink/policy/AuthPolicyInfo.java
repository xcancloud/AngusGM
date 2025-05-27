package cloud.xcan.angus.api.commonlink.policy;

import static cloud.xcan.angus.api.commonlink.AuthConstant.POLICY_PRE_DEFINED_ADMIN_SUFFIX;
import static cloud.xcan.angus.api.commonlink.AuthConstant.POLICY_PRE_DEFINED_EXT_SUFFIX;
import static cloud.xcan.angus.api.commonlink.AuthConstant.POLICY_PRE_DEFINED_GUEST_SUFFIX;
import static cloud.xcan.angus.api.commonlink.AuthConstant.POLICY_PRE_DEFINED_USER_SUFFIX;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Table(name = "auth_policy")
@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyInfo extends TenantAuditingEntity<AuthPolicyInfo, Long> {

  @Id
  private Long id;

  private String name;

  private String code;

  @Enumerated(EnumType.STRING)
  private PolicyType type;

  /**
   * Available for default policy
   */
  private Boolean default0;

  @Enumerated(EnumType.STRING)
  @Column(name = "grant_stage")
  private PolicyGrantStage grantStage;

  private String description;

  @Column(name = "app_id")
  private Long appId;

  @Column(name = "client_id")
  private String clientId;

  private Boolean enabled;

  public boolean isDefaultType() {
    return nonNull(type) && (type.equals(PolicyType.PRE_DEFINED)
        || type.equals(PolicyType.USER_DEFINED));
  }

  public boolean isPlatformType() {
    return nonNull(type) && (type.equals(PolicyType.PRE_DEFINED));
  }

  public boolean isTenantType() {
    return nonNull(type) && type.equals(PolicyType.USER_DEFINED);
  }

  public boolean isPreDefined() {
    return nonNull(type) && type.equals(PolicyType.PRE_DEFINED);
  }

  public boolean isUserDefined() {
    return nonNull(type) && type.equals(PolicyType.USER_DEFINED);
  }

  public boolean hasImmutableValue() {
    return nonNull(code) && nonNull(type)
        || nonNull(grantStage) || nonNull(appId) || nonNull(enabled);
  }

  public String getCodeSuffix() {
    String suffix = substringAfter(code, "_").toUpperCase();
    return isEmpty(suffix) ? "" : "_" + suffix;
  }

  public boolean isAdminPolicy() {
    return nonNull(code) && getCodeSuffix().equals(POLICY_PRE_DEFINED_ADMIN_SUFFIX);
  }

  public boolean isGuestPolicy() {
    return nonNull(code) && getCodeSuffix().equals(POLICY_PRE_DEFINED_GUEST_SUFFIX);
  }

  public boolean isUserPolicy() {
    return nonNull(code) && getCodeSuffix().equals(POLICY_PRE_DEFINED_USER_SUFFIX);
  }

  public boolean isExtPolicy() {
    return nonNull(code) && getCodeSuffix().equals(POLICY_PRE_DEFINED_EXT_SUFFIX);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
