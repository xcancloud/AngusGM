package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.convert;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.gm.domain.app.App;
import java.time.LocalDateTime;

public class AppTagTargetConverter {

  public static WebTagTarget toAppTagTarget(Long tagId, App appDb) {
    WebTagTarget webTagTarget = new WebTagTarget().setTagId(tagId)
        .setTargetId(appDb.getId()).setTargetType(WebTagTargetType.APP);
    webTagTarget.setTenantId(appDb.getTenantId());
    return webTagTarget;
  }

  public static WebTagTarget toFuncTagTarget(Long tagId, AppFunc funcDb) {
    WebTagTarget webTagTarget = new WebTagTarget().setTagId(tagId)
        .setTargetId(funcDb.getId()).setTargetType(funcDb.getType().toTagTargetType());
    webTagTarget.setTenantId(funcDb.getTenantId());
    return webTagTarget;
  }

  public static WebTagTarget objectArrToAppTagTarget(Object[] objects) {
    WebTagTarget orgTagTarget = new WebTagTarget()
        .setId(convert(objects[0], Long.class))
        .setCreatedBy(convert(objects[2], Long.class))
        .setCreatedDate(convert(objects[3], LocalDateTime.class))
        .setTagId(convert(objects[4], Long.class))
        .setTagName(convert(objects[5], String.class))
        .setTargetCreatedBy(convert(objects[6], Long.class))
        .setTargetCreatedDate(convert(objects[7], LocalDateTime.class))
        .setTargetId(convert(objects[8], Long.class))
        .setTargetName(convert(objects[9], String.class))
        .setTargetType(convert(objects[10], WebTagTargetType.class));
    orgTagTarget.setTenantId(convert(objects[1], Long.class));
    return orgTagTarget;
  }
}
