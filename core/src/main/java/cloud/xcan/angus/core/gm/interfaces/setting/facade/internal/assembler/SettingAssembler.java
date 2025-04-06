package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler;

import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_MAX_METRICS_DAYS;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_MAX_RESOURCE_ACTIVITIES;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.indicator.Func;
import cloud.xcan.angus.api.commonlink.setting.indicator.Perf;
import cloud.xcan.angus.api.commonlink.setting.indicator.Stability;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.api.commonlink.setting.social.Social;
import cloud.xcan.angus.api.commonlink.setting.tenant.healthcheck.HealthCheck;
import cloud.xcan.angus.api.gm.indicator.to.FuncTo;
import cloud.xcan.angus.api.gm.indicator.to.PerfTo;
import cloud.xcan.angus.api.gm.indicator.to.StabilityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.SettingValueReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantHealthCheckDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.quota.QuotaTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.security.SecurityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.social.SocialTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.SettingValueVo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant.TenantHealthCheckVo;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SettingAssembler {

  public static Setting updateToSetting(SettingValueReplaceDto dto) {
    return new Setting()
        .setKey(dto.getKey())
        .setSocial(toSocialData(dto.getSocial()))
        .setAiAgent(dto.getAiAgent())
        .setHealthCheck(toHealthCheckData(dto.getHealthCheck()))
        .setApiLog(dto.getApiLog())
        .setOperationLog(dto.getOperationLog())
        .setSystemLog(dto.getSystemLog())
        .setMaxResourceActivities(
            nullSafe(dto.getMaxResourceActivities(), DEFAULT_MAX_RESOURCE_ACTIVITIES))
        .setMaxMetricsDays(nullSafe(dto.getMaxMetricsDays(), DEFAULT_MAX_METRICS_DAYS));
  }

  public static SettingValueVo toSettingValueVo(Setting setting) {
    return new SettingValueVo()
        .setKey(setting.getKey())
        .setGlobalDefault(setting.getGlobalDefault())
        .setLocale(toLocaleTo(setting.getLocale()))
        .setSecurity(toSecurityTo(setting.getSecurity()))
        .setSocial(toSocialTo(setting.getSocial()))
        .setQuota(toQuotaTo(setting.getQuota()))
        .setFunc(toFuncTo(setting.getFunc()))
        .setPerf(toPerfTo(setting.getPerf()))
        .setStability(toStabilityTo(setting.getStability()))
        .setTesterEvent(setting.getTesterEvent())
        .setAiAgent(setting.getAiAgent())
        .setHealthCheck(toHealthCheckTo(setting.getHealthCheck()))
        .setApiLog(setting.getApiLog())
        .setOperationLog(setting.getOperationLog())
        .setSystemLog(setting.getSystemLog())
        .setMaxResourceActivities(setting.getMaxResourceActivities())
        .setMaxMetricsDays(setting.getMaxMetricsDays());
  }

  public static LocaleTo toLocaleTo(Locale locale) {
    return Objects.isNull(locale) ? null :
        new LocaleTo().setDefaultLanguage(locale.getDefaultLanguage())
            .setDefaultTimeZone(locale.getDefaultTimeZone());
  }

  public static SecurityTo toSecurityTo(Security security) {
    return Objects.isNull(security) ? null : new SecurityTo()
        .setSigninLimit(security.getSigninLimit())
        .setSignupAllow(security.getSignupAllow())
        .setPassdPolicy(security.getPasswordPolicy())
        .setAlarm(security.getAlarm());
  }

  public static SocialTo toSocialTo(Social social) {
    return Objects.isNull(social) ? null
        : new SocialTo().setWeChatSocial(social.getWeChatSocial())
            .setGitHubSocial(social.getGitHubSocial())
            .setGoogleSocial(social.getGoogleSocial())
            .setLoginRedirectUrl(social.getLoginRedirectUrl())
            .setLoginBindRedirectUrl(social.getLoginBindRedirectUrl())
            .setBindRedirectUrl(social.getBindRedirectUrl());
  }

  public static Social toSocialData(SocialTo social) {
    return Objects.isNull(social) ? null :
        new Social().setWeChatSocial(social.getWeChatSocial())
            .setGitHubSocial(social.getGitHubSocial())
            .setGoogleSocial(social.getGoogleSocial())
            .setLoginRedirectUrl(social.getLoginRedirectUrl())
            .setLoginBindRedirectUrl(social.getLoginBindRedirectUrl())
            .setBindRedirectUrl(social.getBindRedirectUrl());
  }

  public static List<QuotaTo> toQuotaTo(List<Quota> quotas) {
    return isEmpty(quotas) ? null : quotas.stream().map(quota ->
            new QuotaTo().setAppCode(quota.getAppCode())
                .setServiceCode(quota.getServiceCode())
                .setName(quota.getName())
                .setAllowChange(quota.getAllowChange())
                .setLcsCtrl(quota.getLcsCtrl())
                .setQuota(quota.getQuota())
                .setMin(quota.getMin())
                .setMax(quota.getMax()))
        .collect(Collectors.toList());
  }

  public static FuncTo toFuncTo(Func func) {
    return isEmpty(func) ? null
        : new FuncTo().setSmoke(func.isSmoke())
            .setSmokeCheckSetting(func.getSmokeCheckSetting())
            .setUserDefinedSmokeAssertion(func.getUserDefinedSmokeAssertion())
            .setSecurity(func.isSecurity())
            .setSecurityCheckSetting(func.getSecurityCheckSetting())
            .setUserDefinedSecurityAssertion(func.getUserDefinedSecurityAssertion());
  }

  public static PerfTo toPerfTo(Perf perf) {
    return isEmpty(perf) ? null
        : new PerfTo().setThreads(perf.getThreads())
            .setRampUpThreads(perf.getRampUpThreads())
            .setRampUpInterval(perf.getRampUpInterval())
            .setDuration(perf.getDuration())
            .setArt(perf.getArt())
            .setPercentile(perf.getPercentile())
            .setTps(perf.getTps())
            .setErrorRate(perf.getErrorRate());
  }

  public static StabilityTo toStabilityTo(Stability stability) {
    return isEmpty(stability) ? null
        : new StabilityTo().setThreads(stability.getThreads())
            .setDuration(stability.getDuration())
            .setArt(stability.getArt())
            .setPercentile(stability.getPercentile())
            .setErrorRate(stability.getErrorRate())
            .setTps(stability.getTps())
            .setCpu(stability.getCpu())
            .setDisk(stability.getDisk())
            .setMemory(stability.getMemory())
            .setNetwork(stability.getNetwork());
  }

  public static HealthCheck toHealthCheckData(TenantHealthCheckDto dto) {
    return Objects.isNull(dto) ? null :
        new HealthCheck().setEnabled(dto.getEnabled())
            .setAlarmWay(dto.getAlarmWay())
            .setReceiveUser(dto.getReceiveUser());
  }

  public static TenantHealthCheckVo toHealthCheckTo(HealthCheck data) {
    return Objects.isNull(data) ? null :
        new TenantHealthCheckVo().setEnabled(data.getEnabled())
            .setHealthCheckDate(data.getHealthCheckDate())
            .setAlarmWay(data.getAlarmWay())
            .setReceiveUser(data.getReceiveUser());
  }

}
