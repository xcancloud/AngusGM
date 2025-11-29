package cloud.xcan.angus.api.commonlink.setting;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;


@Getter
public enum SettingKey implements Value<String> {
  LOCALE,
  SECURITY,
  SOCIAL,
  QUOTA,
  /*COMMON_DATA_PERMISSION,*/
  //  FUNC_INDICATOR,
  //  PREF_INDICATOR,
  //  STABILITY_INDICATOR,
  /*EXCEPTION_ALARM,*/
  /*HEALTH_CHECK,*/
  OPERATION_LOG_CONFIG,
  API_LOG_CONFIG,
  SYSTEM_LOG_CONFIG,
  MAX_RESOURCE_ACTIVITIES,
  MAX_METRICS_DAYS,
  //  TESTER_EVENT,
  AI_AGENT;

  @Override
  public String getValue() {
    return this.name();
  }
}
