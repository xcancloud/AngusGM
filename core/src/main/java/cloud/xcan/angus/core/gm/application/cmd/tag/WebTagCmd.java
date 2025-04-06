package cloud.xcan.angus.core.gm.application.cmd.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface WebTagCmd {

  List<IdKey<Long, Object>> add(List<WebTag> tags);

  void update(List<WebTag> webTags);

  void delete(HashSet<Long> ids);

}
