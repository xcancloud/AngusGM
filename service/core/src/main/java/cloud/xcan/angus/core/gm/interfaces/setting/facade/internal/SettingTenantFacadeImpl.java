package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal;


import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.toLocaleTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.toSecurityTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToLocale;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToSecurity;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.security.SecurityTo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SettingTenantFacadeImpl implements SettingTenantFacade {

  @Resource
  private SettingTenantQuery settingTenantQuery;

  @Resource
  private SettingTenantCmd settingTenantCmd;

  @Override
  public void localeReplace(TenantLocaleReplaceDto dto) {
    settingTenantCmd.localeReplace(updateToLocale(dto));
  }

  @Override
  public LocaleTo localeDetail() {
    return toLocaleTo(settingTenantQuery.detail(getOptTenantId()).getLocaleData());
  }

  @Override
  public void securityReplace(SecurityTo dto) {
    settingTenantCmd.securityReplace(updateToSecurity(dto));
  }

  @Override
  public String invitationCodeGen() {
    return settingTenantCmd.invitationCodeGen();
  }

  @Override
  public SecurityTo securityDetail() {
    return toSecurityTo(settingTenantQuery.detail(getOptTenantId()).getSecurityData());
  }

}
