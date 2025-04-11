package cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class EventChannelVo implements Serializable {

  private Long id;

  private ReceiveChannelType channelType;

  private String name;

  private String address;

}
