package cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.security;


import cloud.xcan.angus.api.commonlink.setting.alarm.Alarm;
import cloud.xcan.angus.api.commonlink.setting.security.PasswordPolicy;
import cloud.xcan.angus.api.commonlink.setting.security.SigninLimit;
import cloud.xcan.angus.api.commonlink.setting.security.SignupAllow;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SecurityTo implements Serializable {

  @Valid
  @Schema(description = "Signin security configuration")
  private SigninLimit signinLimit;

  @Valid
  @Schema(description = "Allow signup security configuration")
  private SignupAllow signupAllow;

  @Valid
  @Schema(description = "Password security policy configuration")
  private PasswordPolicy passdPolicy;

  @Valid
  @Schema(description = "Security policy alarm configuration")
  private Alarm alarm;

}
