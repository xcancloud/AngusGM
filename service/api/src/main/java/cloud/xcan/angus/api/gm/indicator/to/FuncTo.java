package cloud.xcan.angus.api.gm.indicator.to;


import cloud.xcan.angus.api.gm.indicator.SecurityCheckSetting;
import cloud.xcan.angus.api.gm.indicator.SmokeCheckSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FuncTo implements Serializable {

  @Schema(description = "Enabled or disabled smoke testing.", defaultValue = "true", requiredMode = RequiredMode.REQUIRED)
  private boolean smoke = true;

  @NotNull
  @Schema(description = "Smoke testing configuration.", requiredMode = RequiredMode.REQUIRED)
  private SmokeCheckSetting smokeCheckSetting;

  @Schema(description = "User-defined check smoke testing assertion.")
  //private Assertion<HttpExtraction> userDefinedSmokeAssertion;
  private Object userDefinedSmokeAssertion;

  @Schema(description = "Enabled or disabled security testing.", defaultValue = "true", requiredMode = RequiredMode.REQUIRED)
  private boolean security = true;

  @NotNull
  @Schema(description = "Security testing configuration.", requiredMode = RequiredMode.REQUIRED)
  private SecurityCheckSetting securityCheckSetting;

  @Schema(description = "User-defined check security testing assertion.")
  //private Assertion<HttpExtraction> userDefinedSecurityAssertion;
  private Object userDefinedSecurityAssertion;

}
