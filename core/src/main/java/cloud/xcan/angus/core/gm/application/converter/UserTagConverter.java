package cloud.xcan.angus.core.gm.application.converter;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;

public class UserTagConverter {

  public static OrgTagTarget toUserTagTarget(Long tagId, User userDb) {
    OrgTagTarget orgTagTarget = new OrgTagTarget()
        .setTagId(tagId)
        .setTargetId(userDb.getId())
        .setTargetType(OrgTargetType.USER);
    orgTagTarget.setTenantId(userDb.getTenantId());
    return orgTagTarget;
  }

}
