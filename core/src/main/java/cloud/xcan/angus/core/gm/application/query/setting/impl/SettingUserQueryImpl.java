package cloud.xcan.angus.core.gm.application.query.setting.impl;

import static cloud.xcan.angus.core.gm.application.converter.SettingUserConverter.assembleProxyConfig;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.user.SettingUser;
import cloud.xcan.angus.api.commonlink.setting.user.SettingUserRepo;
import cloud.xcan.angus.api.commonlink.setting.user.apiproxy.UserApiProxy;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingUserQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.experimental.Assert;
import jakarta.annotation.Resource;
import java.util.Objects;

@Biz
public class SettingUserQueryImpl implements SettingUserQuery {

  @Resource
  private SettingUserRepo settingUserRepo;

  @Resource
  private SettingTenantQuery settingTenantQuery;

  @Override
  public UserApiProxy findProxyByTenantId(Long optTenantId) {
    return new BizTemplate<UserApiProxy>() {

      @Override
      protected UserApiProxy process() {
        SettingTenant tenantSetting = settingTenantQuery.detail(optTenantId);
        Assert.assertNotNull(tenantSetting, "Tenant setting not found");
        return assembleProxyConfig(tenantSetting);
      }
    }.execute();
  }

  @Override
  public SettingUser find(Long userId) {
    return settingUserRepo.findById(userId).orElseThrow(
        () -> ResourceNotFound.of(userId, "UserSetting"));
  }

  @Override
  public SettingUser find0(Long userId) {
    SettingUser settingUser = settingUserRepo.findById(userId).orElse(null);
    if (Objects.isNull(settingUser)) {
      return null;
    }
    return settingUser;
  }
}
