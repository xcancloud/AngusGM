package cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template;

import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel.EventChannelInfoVo;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EventTemplateReceiveVo implements Serializable {

  private EventTemplateReceiverInfoVo receivers;

  private List<EventChannelInfoVo> channels;

}
