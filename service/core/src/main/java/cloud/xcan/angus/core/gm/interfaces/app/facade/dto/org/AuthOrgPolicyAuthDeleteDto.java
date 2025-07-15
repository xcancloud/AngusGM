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
public class AuthOrgPolicyAuthDeleteDto {

  @NotEmpty
  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "Need to delete authorized organization ids", requiredMode = RequiredMode.REQUIRED)
  private HashSet<Long> orgIds;

  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "Delete policy ids. When it is empty, all associated authorizations will be deleted, excluding those of other organizations")
  private HashSet<Long> policyIds;

}
