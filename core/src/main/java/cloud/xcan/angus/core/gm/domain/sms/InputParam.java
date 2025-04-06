package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class InputParam {

  private Set<String> mobiles;

  private SmsBizKey bizKey;

  private Integer expire;

  private Map<String, String> templateParams;

}
