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
  @Schema(description = "Email server identifier for testing", requiredMode = RequiredMode.REQUIRED)
  private Long serverId;

  @NotNull
  @Size(max = MAX_EMAIL_ADDRESS)
  @Schema(description = "List of recipient email addresses for testing. Maximum 500 addresses",
      type = "array", example = "[\"james@xcan.cloud\"]", requiredMode = RequiredMode.REQUIRED)
  private Set<String> toAddress;

  @Schema(description = "Sender tenant identifier. Required for non-user operations (job or innerapi)")
  private Long sendTenantId;

  @Schema(description = "Sender user identifier. Required for non-user operations (job or innerapi)")
  private Long sendUserId;

}
