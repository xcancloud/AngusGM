package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.convert;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import java.time.LocalDateTime;

public class OrgTagTargetConverter {

  public static OrgTagTarget objectArrToOrgTagTarget(Object[] objects) {
    OrgTagTarget orgTagTarget = new OrgTagTarget()
        .setId(convert(objects[0], Long.class))
        .setCreatedBy(convert(objects[2], Long.class))
        .setCreatedDate(convert(objects[3], LocalDateTime.class))
        .setTagId(convert(objects[4], Long.class))
        .setTagName(convert(objects[5], String.class))
        .setTargetCreatedBy(convert(objects[6], Long.class))
        .setTargetCreatedDate(convert(objects[7], LocalDateTime.class))
        .setTargetId(convert(objects[8], Long.class))
        .setTargetName(convert(objects[9], String.class))
        .setTargetType(convert(objects[10], OrgTargetType.class));
    orgTagTarget.setTenantId(convert(objects[1], Long.class));
    return orgTagTarget;
  }
}
