package cloud.xcan.angus.core.gm.application.query.event;

import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;
import java.util.Collection;
import java.util.List;

public interface EventChannelQuery {

  List<EventChannel> channelList(ReceiveChannelType channelType);

  List<ReceiveChannelType> list();

  List<EventChannel> findByTemplateId(Long tenantId, Long templateId);

  EventTemplateReceiver findExecByTemplateId(Long tenantId, Long templateId);

  EventChannel find(Long id);

  List<EventChannel> find(Collection<Long> channelIds);

  void checkNameExisted(EventChannel channel);

  void checkQuota(ReceiveChannelType channelType, int incr);

  void checkNotInUse(Long id);

}
