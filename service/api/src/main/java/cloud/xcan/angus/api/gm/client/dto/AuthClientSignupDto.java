package cloud.xcan.angus.api.gm.client.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class AuthClientSignupDto {

  @NotNull
  @Schema(example = "Privatization client type.", requiredMode = RequiredMode.REQUIRED)
  private Client2pSignupBiz signupBiz;

  @NotNull
  @Schema(description = "Grant tenant id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Grant tenant name.", example = "XCan Technology Company, Ltd",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String tenantName;

  @NotNull
  @Schema(description = "Grant resource id.", requiredMode = RequiredMode.REQUIRED)
  private Long resourceId;

}
