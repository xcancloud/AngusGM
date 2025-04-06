package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal;


import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingAssembler.toFuncTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingAssembler.toPerfTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingAssembler.toStabilityTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.toLocaleTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.toSecurityTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.toServerApiProxyTo;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToApiServerProxy;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToFuncData;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToLocale;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToPerf;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToSecuritt;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantSettingAssembler.updateToStability;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.api.gm.indicator.to.FuncTo;
import cloud.xcan.angus.api.gm.indicator.to.PerfTo;
import cloud.xcan.angus.api.gm.indicator.to.StabilityTo;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuery;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.security.SecurityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.tenant.TenantServerApiProxyTo;
import jakarta.annotation.Resource;
import java.util.List;
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
    settingTenantCmd.securityReplace(updateToSecuritt(dto));
  }

  @Override
  public String invitationCodeGen() {
    return settingTenantCmd.invitationCodeGen();
  }

  @Override
  public SecurityTo securityDetail() {
    return toSecurityTo(settingTenantQuery.detail(getOptTenantId()).getSecurityData());
  }

  @Override
  public void proxyReplace(TenantServerApiProxyTo dto) {
    settingTenantCmd.proxyReplace(updateToApiServerProxy(dto));
  }

  @Override
  public TenantServerApiProxyTo proxyDetail() {
    return toServerApiProxyTo(settingTenantQuery.detail(getOptTenantId()).getServerApiProxyData());
  }

  @Override
  public void testerEventReplace(List<TesterEvent> dto) {
    settingTenantCmd.testerEventReplace(dto);
  }

  @Override
  public List<TesterEvent> testerEventDetail() {
    return settingTenantQuery.detail(getOptTenantId()).getTesterEventData();
  }

  @Override
  public void funcReplace(FuncTo dto) {
    settingTenantCmd.funcReplace(updateToFuncData(dto));
  }

  @Override
  public FuncTo funcDetail() {
    return toFuncTo(settingTenantQuery.detail(getOptTenantId()).getFuncData());
  }

  @Override
  public void perfReplace(PerfTo dto) {
    settingTenantCmd.perfReplace(updateToPerf(dto));
  }

  @Override
  public PerfTo perfDetail() {
    return toPerfTo(settingTenantQuery.detail(getOptTenantId()).getPerfData());
  }

  @Override
  public void stabilityReplace(StabilityTo dto) {
    settingTenantCmd.stabilityReplace(updateToStability(dto));
  }

  @Override
  public StabilityTo stabilityDetail() {
    return toStabilityTo(settingTenantQuery.detail(getOptTenantId()).getStabilityData());
  }
}
