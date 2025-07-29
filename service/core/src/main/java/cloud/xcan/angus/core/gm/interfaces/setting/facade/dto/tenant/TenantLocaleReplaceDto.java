package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TenantLocaleReplaceDto implements Serializable {

  @NotNull
  @Schema(description = "Default language for tenant user interface and communications", defaultValue = "zh_CN", example = "zh_CN", requiredMode = RequiredMode.REQUIRED)
  private SupportedLanguage defaultLanguage;

}
