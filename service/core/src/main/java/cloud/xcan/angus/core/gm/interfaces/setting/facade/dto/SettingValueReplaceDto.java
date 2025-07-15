package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.aiagent.AIAgent;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantHealthCheckDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.social.SocialTo;
import cloud.xcan.angus.core.log.ApiLogProperties;
import cloud.xcan.angus.core.log.OperationLogProperties;
import cloud.xcan.angus.core.log.SystemLogProperties;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SettingValueReplaceDto implements Serializable {

  @NotNull
  @EnumPart(enumClass = SettingKey.class, allowableValues =
      {
          "SOCIAL", "HEALTH_CHECK", "OPERATION_LOG_CONFIG", "API_LOG_CONFIG", "SYSTEM_LOG_CONFIG",
          "SYSTEM_LOG_CLEAR", "MAX_RESOURCE_ACTIVITIES", "MAX_METRICS_DAYS", "AI_AGENT"
      })
  @Schema(description = "The setting key needs to be updated, Only allowed to be modified in the privatized edition",
      requiredMode = RequiredMode.REQUIRED)
  private SettingKey key;

  @Valid
  @Schema(description = "Third-party social account login system configuration")
  private SocialTo social;

  @Valid
  @Schema(description = "AI agent configuration")
  private AIAgent aiAgent;

  // TODO Change to the following Properties format.
  @Valid
  @Schema(description = "Tenant health check notification configuration")
  private TenantHealthCheckDto healthCheck;

  @Schema(description = "API request log configuration")
  private ApiLogProperties apiLog;

  @Schema(description = "System user operation log configuration")
  private OperationLogProperties operationLog;

  @Schema(description = "System log configuration")
  private SystemLogProperties systemLog;

  @Min(0)
  @Schema(description = "Maximum resource activity count", minimum = "0")
  private Integer maxResourceActivities;

  @Min(0)
  @Schema(description = "Maximum metrics retention days", minimum = "0")
  private Integer maxMetricsDays;

}
