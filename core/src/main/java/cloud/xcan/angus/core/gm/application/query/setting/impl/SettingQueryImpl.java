package cloud.xcan.angus.core.gm.application.query.setting.impl;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.SystemAssert;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


@Biz
@Slf4j
public class SettingQueryImpl implements SettingQuery {

  @Resource
  private SettingManager settingManager;

  @Override
  public Setting detail(SettingKey key) {
    return new BizTemplate<Setting>() {

      @Override
      protected Setting process() {
        return settingManager.setting(key);
      }
    }.execute();
  }

  @Override
  public Setting find0(SettingKey key) {
    return settingManager.setting(key);
  }

  @Override
  public Quota checkAndFindQuota(String name) {
    QuotaResource quota;
    try {
      quota = QuotaResource.valueOf(name);
    } catch (Exception e) {
      throw ResourceNotFound.of(name, "QuotaResource");
    }

    List<Quota> quotas = find0(SettingKey.QUOTA).getQuota();
    SystemAssert.assertNotEmpty(quotas, "Quota initialization template not found");

    return quotas.stream().filter(x -> x.getName().equals(quota)).findFirst()
        .orElseThrow(() -> ResourceNotFound.of(name, "QuotaSetting"));
  }

}
