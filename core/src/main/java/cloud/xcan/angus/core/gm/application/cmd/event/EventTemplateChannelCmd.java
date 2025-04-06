package cloud.xcan.angus.core.gm.application.cmd.event;

import java.util.Set;

public interface EventTemplateChannelCmd {

  void channelReplace(Long id, Set<Long> channelIds);
}
