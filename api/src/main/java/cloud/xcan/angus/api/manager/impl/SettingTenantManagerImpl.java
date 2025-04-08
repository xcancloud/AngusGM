package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.isNull;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenantRepo;
import cloud.xcan.angus.api.manager.SettingTenantManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

@Slf4j
@Biz
public class SettingTenantManagerImpl implements SettingTenantManager {

  @Autowired(required = false)
  private SettingTenantRepo settingTenantRepo;

  @Override
  public SettingTenant findSetting(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId).orElse(null);
  }

  @Override
  public SettingTenant findSetting(String invitationCode) {
    return settingTenantRepo.findByInvitationCode(invitationCode).orElse(null);
  }

  @Override
  public SettingTenant checkAndFindSetting(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId)
        .orElseThrow(() -> ResourceNotFound.of(tenantId, "SettingTenant"));
  }

  @Override
  public SettingTenant checkAndFindSetting(String invitationCode) {
    return settingTenantRepo.findByInvitationCode(invitationCode)
        .orElseThrow(() -> ResourceNotFound.of(invitationCode, "SettingTenant"));
  }

  /***
   * Fix: Type id handling not implemented for type cloud.xcan.angus.spec.unit.TimeValue
   */
  @Override
  @Cacheable(key = "'key_' + #tenantId", value = "settingTenant")
  public String getCachedSetting(Long tenantId) {
    SettingTenant settingTenant = settingTenantRepo.findByTenantId(tenantId).orElse(null);
    return isNull(settingTenant) ? null : JsonUtils.toJson(settingTenant);
  }

  @Override
  public SettingTenant parseCachedSetting(String jsonString) {
    return isEmpty(jsonString) ? null : JsonUtils.fromJson(jsonString, SettingTenant.class);
  }

}
