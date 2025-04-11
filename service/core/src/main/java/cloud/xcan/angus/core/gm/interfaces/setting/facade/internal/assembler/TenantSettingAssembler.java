package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.setting.indicator.Func;
import cloud.xcan.angus.api.commonlink.setting.indicator.Perf;
import cloud.xcan.angus.api.commonlink.setting.indicator.Stability;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.api.commonlink.setting.tenant.apiproxy.ServerApiProxy;
import cloud.xcan.angus.api.gm.indicator.to.FuncTo;
import cloud.xcan.angus.api.gm.indicator.to.PerfTo;
import cloud.xcan.angus.api.gm.indicator.to.StabilityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.security.SecurityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.tenant.TenantServerApiProxyTo;
import java.util.Objects;

public class TenantSettingAssembler {

  public static Locale updateToLocale(TenantLocaleReplaceDto dto) {
    return new Locale().setDefaultLanguage(dto.getDefaultLanguage());
  }

  public static LocaleTo toLocaleTo(Locale data) {
    return new LocaleTo().setDefaultLanguage(data.getDefaultLanguage())
        .setDefaultTimeZone(data.getDefaultTimeZone());
  }

  public static Security updateToSecurity(SecurityTo dto) {
    return new Security().setSigninLimit(dto.getSigninLimit())
        .setPasswordPolicy(dto.getPasswordPolicy())
        .setSignupAllow(dto.getSignupAllow()).setAlarm(dto.getAlarm());
  }

  public static SecurityTo toSecurityTo(Security data) {
    return new SecurityTo().setSigninLimit(data.getSigninLimit())
        .setPasswordPolicy(data.getPasswordPolicy())
        .setSignupAllow(data.getSignupAllow()).setAlarm(data.getAlarm());
  }

  public static TenantServerApiProxyTo toServerApiProxyTo(ServerApiProxy data) {
    if (Objects.isNull(data)) {
      return null;
    }
    return new TenantServerApiProxyTo().setUrl(data.getUrl()).setEnabled(data.getEnabled());
  }

  public static ServerApiProxy updateToApiServerProxy(TenantServerApiProxyTo to) {
    return new ServerApiProxy()
        .setUrl(to.getUrl().startsWith("ws") ? to.getUrl() : "ws://" + to.getUrl())
        .setEnabled(to.getEnabled());
  }

  public static Func updateToFuncData(FuncTo func) {
    return new Func()
        .setSmoke(func.isSmoke())
        .setSmokeCheckSetting(func.getSmokeCheckSetting())
        .setUserDefinedSmokeAssertion(func.getUserDefinedSmokeAssertion())
        .setSecurity(func.isSecurity())
        .setSecurityCheckSetting(func.getSecurityCheckSetting())
        .setUserDefinedSecurityAssertion(func.getUserDefinedSecurityAssertion());
  }

  public static Perf updateToPerf(PerfTo perf) {
    return new Perf()
        .setThreads(perf.getThreads())
        .setRampUpThreads(perf.getRampUpThreads())
        .setRampUpInterval(perf.getRampUpInterval())
        .setDuration(perf.getDuration())
        .setArt(perf.getArt())
        .setPercentile(perf.getPercentile())
        .setTps(perf.getTps())
        .setErrorRate(perf.getErrorRate());
  }

  public static Stability updateToStability(StabilityTo stability) {
    return new Stability()
        .setThreads(stability.getThreads())
        .setDuration(stability.getDuration())
        .setArt(stability.getArt())
        .setPercentile(stability.getPercentile())
        .setTps(stability.getTps())
        .setErrorRate(stability.getErrorRate())
        .setCpu(stability.getCpu())
        .setDisk(stability.getDisk())
        .setMemory(stability.getMemory())
        .setNetwork(stability.getNetwork());
  }

}
