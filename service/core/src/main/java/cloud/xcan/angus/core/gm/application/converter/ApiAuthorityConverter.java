package cloud.xcan.angus.core.gm.application.converter;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthority;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthoritySource;

public class ApiAuthorityConverter {

  public static ApiAuthority toAppAuthority(App app, Api api, Long id) {
    return new ApiAuthority().setId(id)
        .setSource(ApiAuthoritySource.APP)
        .setSourceId(app.getId()).setSourceEnabled(app.getEnabled())
        .setAppId(app.getId()).setAppEnabled(app.getEnabled())
        .setApiId(api.getId()).setApiOperationId(api.getOperationId())
        .setApiEnabled(api.getEnabled())
        .setServiceId(api.getServiceId()).setServiceCode(api.getServiceCode())
        .setServiceEnabled(null);
  }

  public static ApiAuthority toFuncAuthority(App app, AppFunc func, Api api, Long id) {
    return new ApiAuthority().setId(id)
        .setSource(ApiAuthoritySource.APP_FUNC).setSourceId(func.getId())
        .setSourceEnabled(func.getEnabled())
        .setAppId(app.getId()).setAppEnabled(app.getEnabled())
        .setApiId(api.getId()).setApiOperationId(api.getOperationId())
        .setApiEnabled(api.getEnabled())
        .setServiceId(api.getServiceId()).setServiceCode(api.getServiceCode())
        .setServiceEnabled(api.getServiceEnabled());
  }
}
