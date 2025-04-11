package cloud.xcan.angus.api.gm.indicator.to;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.Percentile;
import cloud.xcan.angus.spec.annotations.Beta;
import cloud.xcan.angus.spec.unit.TimeValue;
import cloud.xcan.angus.validator.TimeValueRange;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Valid
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StabilityTo implements Serializable {

  @NotNull
  @Min(1)
  @Schema(description = "Number of concurrent threads(VU).", minimum = "1",
      requiredMode = RequiredMode.REQUIRED)
  private Integer threads;

  @TimeValueRange(minInMs = 1000/*, maxInMs = MAX_EXEC_DURATION_IN_MS*/) // 1 days
  @NotNull
  @Schema(description =
      "Duration of task execution, when iterations and duration are not configured, "
          + "they will automatically execute for 5 minutes.", requiredMode = RequiredMode.REQUIRED)
  private TimeValue duration;

  @NotNull
  @Min(1)
  @Schema(description = "Operations per second(QPS/TPS).", minimum = "1",
      requiredMode = RequiredMode.REQUIRED)
  private Integer tps;

  @Min(1)
  @Schema(description = "Average response time in milliseconds(ART,ms).", minimum = "1")
  private Long art;

  @Schema(description = "Average response time percentile.")
  private Percentile percentile;

  @NotNull
  @DecimalMax(value = "100.00")
  @Schema(description = "Error rate (ERROR)/%.", minimum = "0", maximum = "100",
      requiredMode = RequiredMode.REQUIRED)
  private Double errorRate;

  @DecimalMax(value = "100.00")
  @Schema(description = "Application node CPU usage rate.", minimum = "0", maximum = "100")
  private Double cpu;

  @DecimalMax(value = "100.00")
  @Schema(description = "Application node memory usage rate.", minimum = "0", maximum = "100")
  private Double memory;

  @DecimalMax(value = "100.00")
  @Schema(description = "Application node disk usage rate.", minimum = "0", maximum = "100")
  private Double disk;

  @Beta // Not fully supported
  @DecimalMax(value = "10000.00")
  @Schema(description = "Application node network usage rate.", minimum = "0", maximum = "10000")
  private Double network;

  public boolean hasNodeUsage() {
    return nonNull(cpu) || nonNull(memory) || nonNull(disk) || nonNull(network);
  }

}
