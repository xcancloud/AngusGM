package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.AuthConstant.APP_OPEN_YEAR;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.time.LocalDateTime;

public class AppOpenConverter {

  public static AppOpen toAppOpen(App appDb, Long tenantId) {
    return new AppOpen().setAppId(appDb.getId())
        .setAppCode(appDb.getCode())
        .setVersion(appDb.getVersion())
        .setEditionType(appDb.getEditionType())
        .setTenantId(tenantId)
        .setUserId(PrincipalContext.getUserId())
        .setOpenDate(LocalDateTime.now())
        .setExpirationDate(LocalDateTime.now().plusYears(APP_OPEN_YEAR));
  }
}
