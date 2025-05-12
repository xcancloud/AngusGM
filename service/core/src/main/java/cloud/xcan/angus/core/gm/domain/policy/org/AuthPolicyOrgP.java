package cloud.xcan.angus.core.gm.domain.policy.org;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthPolicyOrgP {

  private Long policyId;

  @Enumerated(EnumType.STRING)
  private AuthOrgType orgType;

  private Long orgId;

}
