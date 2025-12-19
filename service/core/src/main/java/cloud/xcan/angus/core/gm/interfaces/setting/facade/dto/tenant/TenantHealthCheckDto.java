package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant;

import static cloud.xcan.angus.api.commonlink.CommonConstant.MAX_HEALTH_ALARM_RECIPIENT;

import cloud.xcan.angus.api.enums.AlarmWay;
import cloud.xcan.angus.api.pojo.UserName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TenantHealthCheckDto implements Serializable {

  @Schema(description = "Enable tenant health monitoring and alerting system", defaultValue = "false", example = "true")
  private Boolean enabled = false;

  @Schema(description = "Alert notification methods for health check abnormalities", allowableValues = {
      "SMS", "EMAIL"})
  private List<AlarmWay> alarmWay;

  @Size(max = MAX_HEALTH_ALARM_RECIPIENT)
  @Schema(description = "Health check alert recipients. Supports up to 50 users for notification delivery")
  private List<UserName> receiveUser;

}

