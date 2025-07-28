package cloud.xcan.angus.api.gm.policy.vo;

import cloud.xcan.angus.api.commonlink.policy.AuthOrgPolicyP;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthPolicyOrgVo {

  @Schema(description = "Policy id")
  private Long id;

  @Schema(description = "Policy name")
  private String name;

  @Schema(description = "Policy code")
  private String code;

  @Schema(description = "Policy description")
  private String description;

  @Schema(description = "Authorization policy source")
  private List<AuthOrgPolicyP> authOrgs;

}
