package cloud.xcan.angus.core.gm.application.cmd.notice;

import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface NoticeCmd {

  IdKey<Long, Object> add(Notice notice);

  void delete(List<Long> ids);
}
