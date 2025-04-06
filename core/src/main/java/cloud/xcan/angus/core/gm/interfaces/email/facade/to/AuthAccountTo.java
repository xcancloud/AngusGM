package cloud.xcan.angus.core.gm.interfaces.email.facade.to;


import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_PASSWORD_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class AuthAccountTo implements Serializable {

  @NotBlank
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "Authentication account username.", example = "test@xcan.cloud",
      maxLength = MAX_EMAIL_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String account;

  @Length(max = MAX_PASSWORD_LENGTH)
  @Schema(description = "Authentication account password.", example = "xcan@123",
      maxLength = MAX_PASSWORD_LENGTH)
  private String password;

}
