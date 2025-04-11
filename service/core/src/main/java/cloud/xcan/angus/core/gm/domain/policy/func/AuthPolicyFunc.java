package cloud.xcan.angus.core.gm.domain.policy.func;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


@Setter
@Getter
@Accessors(chain = true)
@Entity
@Table(name = "auth_policy_func")
@EntityListeners({AuditingEntityListener.class})
public class AuthPolicyFunc extends TenantEntity<AuthPolicyFunc, Long> {

  @Id
  private Long id;

  @Column(name = "policy_id")
  private Long policyId;

  @Column(name = "app_id")
  private Long appId;

  @Column(name = "func_id")
  private Long funcId;

  @Enumerated(EnumType.STRING)
  @Column(name = "func_type")
  private AppFuncType funcType;

  @CreatedBy
  @Column(name = "created_by")
  private Long createdBy;

  @CreatedDate
  @DateTimeFormat(pattern = DATE_FMT)
  @Column(name = "created_date", updatable = false, columnDefinition = "TIMESTAMP")
  private Date createdDate;

  @Transient
  private AppFunc appFunc;
  @Transient
  private List<Api> apis;

  @Override
  public Long identity() {
    return this.id;
  }

}
