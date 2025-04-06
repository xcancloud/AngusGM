package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_CLOUD_API_PROXY;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_LOCAL_API_PROXY;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.ApiProxy;
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.ApiProxyType;
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.UserApiProxy;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import java.util.Objects;

public class SettingUserConverter {

  public static SettingUser initUserSetting(Long tenantId, SettingTenant tenantSetting) {
    SettingUser user = new SettingUser();
    user.setTenantId(tenantId);
    Preference preferenceData = new Preference()
        .setLanguage(tenantSetting.getLocaleData().getDefaultLanguage())
        .setDefaultTimeZone(getApplicationInfo().getTimezone());
    user.setPreference(preferenceData);

    UserApiProxy apiProxyData = assembleProxyConfig(tenantSetting);
    user.setApiProxy(apiProxyData);

    return user;
  }

  public static UserApiProxy assembleProxyConfig(SettingTenant tenantSetting) {
    return new UserApiProxy()
        .setNoProxy(new ApiProxy()
            .setName(ApiProxyType.NO_PROXY)
            .setEnabled(false)
            .setUrl(null))
        .setClientProxy(new ApiProxy()
            .setName(ApiProxyType.CLIENT_PROXY)
            .setEnabled(false)
            .setUrl(DEFAULT_LOCAL_API_PROXY))
        .setServerProxy(new ApiProxy()
            .setName(ApiProxyType.SERVER_PROXY)
            .setEnabled(Objects.nonNull(tenantSetting.getServerApiProxyData()))
            .setUrl(Objects.nonNull(tenantSetting.getServerApiProxyData()) ? tenantSetting
                .getServerApiProxyData().getUrl() : null))
        .setCloudProxy(new ApiProxy()
            .setName(ApiProxyType.CLOUD_PROXY)
            .setEnabled(Objects.isNull(tenantSetting.getServerApiProxyData()))
            .setUrl(DEFAULT_CLOUD_API_PROXY)
        );
  }

}
