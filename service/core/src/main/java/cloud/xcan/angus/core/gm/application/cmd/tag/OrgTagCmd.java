package cloud.xcan.angus.core.gm.application.cmd.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface OrgTagCmd {

  List<IdKey<Long, Object>> add(List<OrgTag> tags);

  void update(List<OrgTag> tags);

  void delete(HashSet<Long> ids);

}
