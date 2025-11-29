package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.aiagent.AIAgent;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantHealthCheckDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.social.SocialTo;
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
  @Schema(description = "System setting key to update. Only allowed to be modified in the privatized edition",
      requiredMode = RequiredMode.REQUIRED)
  private SettingKey key;

  @Valid
  @Schema(description = "Third-party social account login system configuration for authentication")
  private SocialTo social;

  @Valid
  @Schema(description = "AI agent configuration for intelligent service automation")
  private AIAgent aiAgent;

  // TODO Change to the following Properties format.
  @Valid
  @Schema(description = "Tenant health check notification configuration for system monitoring")
  private TenantHealthCheckDto healthCheck;

  @Schema(description = "API request logging configuration for debugging and monitoring")
  private ApiLogProperties apiLog;

  @Schema(description = "System user operation logging configuration for audit trails")
  private OperationLogProperties operationLog;

  @Schema(description = "System log configuration for infrastructure monitoring")
  private SystemLogProperties systemLog;

  @Min(0)
  @Schema(description = "Maximum resource activity count for performance optimization", minimum = "0")
  private Integer maxResourceActivities;

  @Min(0)
  @Schema(description = "Maximum metrics retention days for data management", minimum = "0")
  private Integer maxMetricsDays;

}
