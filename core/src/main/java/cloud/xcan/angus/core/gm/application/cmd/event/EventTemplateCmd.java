package cloud.xcan.angus.core.gm.application.cmd.event;

import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.spec.experimental.IdKey;


public interface EventTemplateCmd {

  IdKey<Long, Object> add(EventTemplate template);

  IdKey<Long, Object> replace(EventTemplate template);

  void delete(Long id);

}
