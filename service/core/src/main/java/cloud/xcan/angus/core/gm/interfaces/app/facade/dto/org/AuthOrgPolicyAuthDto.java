package cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthOrgPolicyAuthDto {

  @NotEmpty
  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "Need to authorize organization ids, supports a maximum of `500`",
      requiredMode = RequiredMode.REQUIRED)
  private HashSet<Long> orgIds;

  @NotEmpty
  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "Authorize policy ids, supports a maximum of `500`",
      requiredMode = RequiredMode.REQUIRED)
  private HashSet<Long> policyIds;

}
