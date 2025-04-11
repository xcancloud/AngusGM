package cloud.xcan.angus.api.commonlink.setting.indicator;


import cloud.xcan.angus.api.gm.indicator.SecurityCheckSetting;
import cloud.xcan.angus.api.gm.indicator.SmokeCheckSetting;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Valid
@Getter
@Setter
@Accessors(chain = true)
public class Func extends ValueObjectSupport<Func> {

  @Schema(description = "Enabled or disabled smoke testing.", defaultValue = "true")
  private boolean smoke;

  @Schema(description = "Enabled or disabled smoke testing.")
  private SmokeCheckSetting smokeCheckSetting;

  @Schema(description = "User-defined check smoke testing assertion.")
  //private Assertion<HttpExtraction> userDefinedSmokeAssertion;
  private Object userDefinedSmokeAssertion;

  @Schema(description = "Enabled or disabled security testing.", defaultValue = "true")
  private boolean security;

  @Schema(description = "Enabled or disabled security testing.")
  private SecurityCheckSetting securityCheckSetting;

  @Schema(description = "User-defined check security testing assertion.")
  //private Assertion<HttpExtraction> userDefinedSecurityAssertion;
  private Object userDefinedSecurityAssertion;

  public Func() {
  }

  public Func(boolean smoke, SmokeCheckSetting smokeCheckSetting,
      Object userDefinedSmokeAssertion, boolean security,
      SecurityCheckSetting securityCheckSetting,
      Object userDefinedSecurityAssertion) {
    this.smoke = smoke;
    this.smokeCheckSetting = smokeCheckSetting;
    this.userDefinedSmokeAssertion = userDefinedSmokeAssertion;
    this.security = security;
    this.securityCheckSetting = securityCheckSetting;
    this.userDefinedSecurityAssertion = userDefinedSecurityAssertion;
  }

  public static Func default0() {
    return new Func(true, SmokeCheckSetting.API_AVAILABLE, null, true,
        SecurityCheckSetting.NOT_SECURITY_CODE, null);
  }

}
