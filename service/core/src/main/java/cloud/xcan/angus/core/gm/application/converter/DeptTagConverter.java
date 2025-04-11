package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeptTagConverter {

  public static List<OrgTagTarget> dtoToDeptTagsDomain(List<Long> tagIds, Long deptId) {
    return isEmpty(tagIds) ? new ArrayList<>() : tagIds.stream().map(tagId -> new OrgTagTarget()
        .setTargetType(OrgTargetType.DEPT).setTargetId(deptId)
        .setTagId(tagId).setCreatedBy(-1L)).collect(Collectors.toList());
  }

  public static OrgTagTarget toDeptTagTarget(Long tagId, Dept deptDb) {
    OrgTagTarget orgTagTarget = new OrgTagTarget().setTagId(tagId)
        .setTargetId(deptDb.getId())
        .setTargetType(OrgTargetType.DEPT);
    orgTagTarget.setTenantId(deptDb.getTenantId());
    return orgTagTarget;
  }
}
