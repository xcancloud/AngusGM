package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_EMAIL_ADDRESS;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class EmailTestDto implements Serializable {

  @NotNull
  @Schema(description = "Test email server id.", requiredMode = RequiredMode.REQUIRED)
  private Long serverId;

  @NotNull
  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of email receiving addresses, supporting up to `500` addresses. ",
      type = "array", example = "[\"james@xcan.cloud\"]", requiredMode = RequiredMode.REQUIRED)
  private Set<String> toAddress;

  @Schema(description = "Send tenant id. Non user operation (job or doorapi) is required.")
  private Long sendTenantId;

  @Schema(description = "Send user id. Non user operation (job or doorapi) is required.")
  private Long sendUserId;

}
