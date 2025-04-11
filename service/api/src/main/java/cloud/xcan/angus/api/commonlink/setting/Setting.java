package cloud.xcan.angus.api.commonlink.setting;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.aiagent.AIAgent;
import cloud.xcan.angus.api.commonlink.setting.alarm.Alarm;
import cloud.xcan.angus.api.commonlink.setting.indicator.Func;
import cloud.xcan.angus.api.commonlink.setting.indicator.Perf;
import cloud.xcan.angus.api.commonlink.setting.indicator.Stability;
import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.api.commonlink.setting.social.Social;
import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.api.commonlink.setting.tenant.healthcheck.HealthCheck;
import cloud.xcan.angus.core.log.ApiLogProperties;
import cloud.xcan.angus.core.log.OperationLogProperties;
import cloud.xcan.angus.core.log.SystemLogProperties;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Entity
@Table(name = "c_setting")
@Setter
@Getter
@Accessors(chain = true)
public class Setting extends EntitySupport<Setting, Long> implements Serializable {

  @Id
  private Long id;

  @Enumerated(EnumType.STRING)
  private SettingKey key;

  private String value;

  @Column(name = "global_default")
  private Boolean globalDefault;

  // @Transient -> transient <- Json and jpa both ignore
  private transient Locale locale;
  private transient Security security;
  private transient Social social;
  private transient List<Quota> quota;
  private transient Func func;
  private transient Perf perf;
  private transient Stability stability;
  private transient List<TesterEvent> testerEvent;
  private transient AIAgent aiAgent;
  /**
   * @see HealthCheck
   */
  @Deprecated
  private transient Alarm exceptionAlarm;
  private transient HealthCheck healthCheck;
  private transient ApiLogProperties apiLog;
  private transient OperationLogProperties operationLog;
  private transient SystemLogProperties systemLog;
  private transient Integer maxResourceActivities;
  private transient Integer maxMetricsDays;

  public Quota findQuotaByName(String name) {
    return isEmpty(quota) ? null
        : quota.stream().filter(x -> x.getName().getValue().equals(name)).findFirst()
            .orElse(null);
  }

  public Map<String, Quota> toQuotaMap() {
    return isEmpty(quota) ? null
        : quota.stream().collect(Collectors.toMap(x -> x.getName().getValue(), x -> x));
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
