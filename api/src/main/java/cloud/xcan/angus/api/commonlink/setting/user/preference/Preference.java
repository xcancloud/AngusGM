package cloud.xcan.angus.api.commonlink.setting.user.preference;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class Preference extends ValueObjectSupport<Preference> {

  @Schema(description = "The code of subject set by user.", example = "gray")
  private String themeCode;

  @Schema(description = "Language set by user.", example = "zh_CN")
  private SupportedLanguage language;

  /**
   * The time zone is not allowed to be modified and Load by configuration.
   */
  @JsonIgnore
  @Schema(description = "Default system time zone.", example = "Asia/Shanghai")
  private transient String defaultTimeZone;
}
