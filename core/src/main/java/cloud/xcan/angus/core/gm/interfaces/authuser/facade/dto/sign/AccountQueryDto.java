package cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.validator.Passd;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
public class AccountQueryDto {

  @Passd
  @Schema(description = "User login password.", example = "xcan@123", requiredMode = RequiredMode.REQUIRED)
  private String password;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "User login account ( your username, mobile or email).",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String account;

}
