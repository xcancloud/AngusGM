package cloud.xcan.angus.core.gm.interfaces.setting.facade.to.locale;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class LocaleTo implements Serializable {

  @NotNull
  @Schema(description = "Default language", defaultValue = "zh_CN", example = "zh_CN", requiredMode = RequiredMode.REQUIRED)
  private SupportedLanguage defaultLanguage;

  @Length(max = 20)
  @Schema(description = "The time zone is not allowed to be modified and Load by configuration", example = "Asia/Shanghai")
  private String defaultTimeZone;

}
