package cloud.xcan.angus.api.commonlink.setting.locale;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Locale extends ValueObjectSupport<Locale> {

  private SupportedLanguage defaultLanguage;

  // NOOP(The time zone is not allowed to be modified and Load by configuration):
  @JsonIgnore
  private transient String defaultTimeZone;

}
