package cloud.xcan.angus.core.gm.application.cmd.event;

import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;

public interface EventTemplateReceiverCmd {

  void receiverReplace(EventTemplateReceiver receiver);
}
