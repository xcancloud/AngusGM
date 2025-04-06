package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupTagConverter {

  public static OrgTagTarget toGroupTagTarget(Long tagId, Group groupDb) {
    OrgTagTarget orgTagTarget = new OrgTagTarget()
        .setTagId(tagId)
        .setTargetId(groupDb.getId())
        .setTargetType(OrgTargetType.GROUP);
    orgTagTarget.setTenantId(groupDb.getTenantId());
    return orgTagTarget;
  }

  public static List<OrgTagTarget> dtoToGroupTagsDomain(List<Long> tagIds, Long groupId) {
    return isEmpty(tagIds) ? new ArrayList<>() : tagIds.stream().map(tagId -> new OrgTagTarget()
            .setTargetType(OrgTargetType.GROUP)
            .setTargetId(groupId)
            .setTagId(tagId)
            .setCreatedBy(-1L))
        .collect(Collectors.toList());
  }

}
