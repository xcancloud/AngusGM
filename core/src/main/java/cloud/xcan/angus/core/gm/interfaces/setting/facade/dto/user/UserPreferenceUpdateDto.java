package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user;

import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserPreferenceUpdateDto implements Serializable {

  @Schema(description = "Language set by user", example = "zh_CN")
  private SupportedLanguage language;

}
