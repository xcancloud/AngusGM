package cloud.xcan.angus.api.commonlink.setting.tenant.healthcheck;

import cloud.xcan.angus.api.enums.AlarmWay;
import cloud.xcan.angus.api.pojo.UserName;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class HealthCheck extends ValueObjectSupport<HealthCheck> {

  @Schema(description = "Enable health check.", defaultValue = "true")
  private Boolean enabled = true;

  @Schema(description = "Alarm way when abnormal.")
  private List<AlarmWay> alarmWay;

  @Schema(description = "Abnormal alarm recipient.")
  private List<UserName> receiveUser;

  @Schema(description = "Last health check date.")
  private LocalDateTime healthCheckDate;

}
