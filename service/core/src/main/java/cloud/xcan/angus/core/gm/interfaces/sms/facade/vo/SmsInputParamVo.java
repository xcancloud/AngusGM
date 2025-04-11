package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class SmsInputParamVo implements Serializable {

  private Set<String> mobiles;

  private String serviceCode;

  private SmsBizKey bizKey;

  private Integer expire;

  private Map<String, String> templateParams;

}
