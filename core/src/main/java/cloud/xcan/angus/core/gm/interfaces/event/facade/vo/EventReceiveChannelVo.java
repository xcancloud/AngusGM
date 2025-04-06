package cloud.xcan.angus.core.gm.interfaces.event.facade.vo;

import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel.EventChannelVo;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class EventReceiveChannelVo implements Serializable {

  private ReceiveChannelType channelType;

  private List<EventChannelVo> channels;

}
