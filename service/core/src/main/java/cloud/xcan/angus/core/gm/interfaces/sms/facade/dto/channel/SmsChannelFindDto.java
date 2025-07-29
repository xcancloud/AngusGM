package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel;

import cloud.xcan.angus.remote.PageQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SmsChannelFindDto extends PageQuery {

  // This DTO is currently empty but may be extended with filtering parameters in the future
  // For now, it inherits pagination functionality from PageQuery

}
