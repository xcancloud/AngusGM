package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WebTagConverter {

  public static List<WebTagTarget> assembleAppOrFuncTags(Long targetId,
      WebTagTargetType targetType, Set<Long> tagIds) {
    List<WebTagTarget> tagTargets = new ArrayList<>();
    if (isNotEmpty(tagIds)) {
      tagIds.forEach(tagId -> {
        tagTargets.add(new WebTagTarget().setTagId(tagId)
            .setTargetType(targetType).setTargetId(targetId));
      });
    }
    return tagTargets;
  }

}
