package cloud.xcan.angus.api.commonlink.setting.security;

import cloud.xcan.angus.api.commonlink.setting.alarm.Alarm;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The invitation code must be unique and initialized after registration.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class Security extends ValueObjectSupport<Security> {

  @Schema(description = "Sign-in security configuration.")
  private SigninLimit signinLimit;

  @Schema(description = "Allow signup security configuration.")
  private SignupAllow signupAllow;

  @Schema(description = "Password security policy configuration.")
  private PasswordPolicy passwordPolicy;

  @Schema(description = "Security policy alarm configuration.")
  private Alarm alarm;

}
