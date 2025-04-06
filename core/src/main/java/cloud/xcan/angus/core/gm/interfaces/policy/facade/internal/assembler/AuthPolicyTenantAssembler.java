package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.org.AuthAppDefaultPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.org.AuthDefaultPolicyVo;
import java.util.List;
import java.util.stream.Collectors;


public class AuthPolicyTenantAssembler {

  public static List<AuthAppDefaultPolicyVo> toDefaultPolicyVo(List<AuthPolicyOrg> policyOrgs) {
    if (isEmpty(policyOrgs)) {
      return null;
    }
    return policyOrgs.stream()
        .map(x -> new AuthAppDefaultPolicyVo().setAppId(x.getApp().getId())
            .setAppCode(x.getApp().getCode())
            .setAppType(x.getApp().getType())
            .setAppName(x.getApp().getName())
            .setVersion(x.getApp().getVersion())
            .setDefaultPolicies(
                x.getDefaultPolices().stream().map(y -> new AuthDefaultPolicyVo()
                    .setId(y.getId()).setCode(y.getCode()).setName(y.getName()).setType(y.getType())
                    .setCurrentDefault(y.getCurrentDefault0())
                    .setDescription(y.getDescription())
                ).collect(Collectors.toList()))
        ).collect(Collectors.toList());
  }

}
