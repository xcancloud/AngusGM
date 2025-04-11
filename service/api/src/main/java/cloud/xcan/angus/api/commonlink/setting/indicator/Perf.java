package cloud.xcan.angus.api.commonlink.setting.indicator;

import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_ART;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_ERROR_RATE;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PERCENTILE;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PERF_DURATION;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PERF_RAMP_UP_INTERVAL;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PERF_RAMP_UP_THREADS;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PERF_THREADS;
import static cloud.xcan.angus.api.commonlink.CommonConstant.DEFAULT_PERF_TPS;

import cloud.xcan.angus.api.enums.Percentile;
import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import cloud.xcan.angus.spec.unit.TimeValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class Perf extends ValueObjectSupport<Perf> {

  @Schema(description = "Number of concurrent threads(VU)")
  private Integer threads;

  @Schema(description = "Duration of task execution, when iterations and duration are not configured, they will automatically execute for 30 seconds.")
  private TimeValue duration;

  @Schema(description = "Adjust ramp up thread number, the value does not exceed the threads.")
  private Integer rampUpThreads;

  @Schema(description = "Adjust ramp up thread interval, the value does not exceed the duration.")
  private TimeValue rampUpInterval;

  @Schema(description = "Average response time in milliseconds(ART,ms)")
  private Long art;

  @Schema(description = "Average response time percentile")
  private Percentile percentile;

  @Schema(description = "Operations per second(QPS/TPS)")
  private Integer tps;

  @Schema(description = "Error rate (ERROR)/%")
  private Double errorRate;

  public Perf() {
  }

  public Perf(Integer threads, TimeValue duration, Integer rampUpThreads,
      TimeValue rampUpInterval, Long art, Percentile percentile, Integer tps, Double errorRate) {
    this.threads = threads;
    this.duration = duration;
    this.rampUpThreads = rampUpThreads;
    this.rampUpInterval = rampUpInterval;
    this.art = art;
    this.percentile = percentile;
    this.tps = tps;
    this.errorRate = errorRate;
  }

  public static Perf default0() {
    return new Perf(DEFAULT_PERF_THREADS, DEFAULT_PERF_DURATION, DEFAULT_PERF_RAMP_UP_THREADS,
        DEFAULT_PERF_RAMP_UP_INTERVAL, DEFAULT_ART, DEFAULT_PERCENTILE, DEFAULT_PERF_TPS,
        DEFAULT_ERROR_RATE.doubleValue());
  }
}
