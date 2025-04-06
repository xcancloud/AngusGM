package cloud.xcan.angus.api.gm.setting.vo;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserPreferenceVo implements Serializable {

  @Schema(description = "The code of subject set by user.", example = "gray")
  private String themeCode;

  // List<ThemeTo> themes;

  @Schema(description = "Language set by user.", example = "zh_CN")
  private SupportedLanguage language;

  @Schema(description = "Default system time zone.", example = "Asia/Shanghai")
  private String defaultTimeZone;

}
