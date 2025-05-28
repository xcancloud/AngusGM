package cloud.xcan.angus.api.gm.indicator.to;


import cloud.xcan.angus.api.enums.Percentile;
import cloud.xcan.angus.spec.unit.TimeValue;
import cloud.xcan.angus.validator.TimeValueRange;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PerfTo implements Serializable {

  @NotNull
  @Min(1)
  @Schema(description = "Number of concurrent threads(VU).", minimum = "1", requiredMode = RequiredMode.REQUIRED)
  private Integer threads;

  @TimeValueRange(minInMs = 1000/*, maxInMs = MAX_EXEC_DURATION_IN_MS*/) // 1 days
  @NotNull
  @Schema(description =
      "Duration of task execution, when iterations and duration are not configured, "
          + "they will automatically execute for `30` seconds.", requiredMode = RequiredMode.REQUIRED)
  private TimeValue duration;

  @Min(0)
  @Schema(description = "Adjust ramp up thread number, the value does not exceed the threads.", minimum = "0")
  private Integer rampUpThreads;

  @Schema(description = "Adjust ramp up thread interval, the value does not exceed the duration.")
  private TimeValue rampUpInterval;

  @Min(1)
  @Schema(description = "Average response time in milliseconds(ART,ms).", minimum = "1")
  private Long art;

  @Schema(description = "Average response time percentile.")
  private Percentile percentile;

  @NotNull
  @Min(1)
  @Schema(description = "Operations per second(QPS/TPS).", minimum = "1", requiredMode = RequiredMode.REQUIRED)
  private Integer tps;

  @NotNull
  @DecimalMax(value = "100.00")
  @Schema(description = "Error rate (ERROR)/%.", minimum = "0", maximum = "100", requiredMode = RequiredMode.REQUIRED)
  private Double errorRate;

}
