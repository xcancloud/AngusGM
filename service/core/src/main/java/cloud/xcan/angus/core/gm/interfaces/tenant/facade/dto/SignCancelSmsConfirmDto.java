package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_VERIFICATION_CODE_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class SignCancelSmsConfirmDto implements Serializable {

  @NotBlank
  @Length(max = MAX_VERIFICATION_CODE_LENGTH)
  @Schema(description = "Sms verification code", example = "778717", requiredMode = RequiredMode.REQUIRED)
  private String verificationCode;

}
