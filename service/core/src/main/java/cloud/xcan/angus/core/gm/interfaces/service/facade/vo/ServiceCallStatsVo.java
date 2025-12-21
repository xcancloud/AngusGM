package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Service call stats VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ServiceCallStatsVo implements Serializable {

  private String serviceName;

  private Period period;

  private Long totalRequests;

  private Long successRequests;

  private Long failedRequests;

  private Integer avgResponseTime;

  private Integer maxResponseTime;

  private Integer minResponseTime;

  private List<DailyCount> requestsPerDay;

  @Getter
  @Setter
  @Accessors(chain = true)
  public static class Period implements Serializable {
    private LocalDate startDate;
    private LocalDate endDate;
  }

  @Getter
  @Setter
  @Accessors(chain = true)
  public static class DailyCount implements Serializable {
    private LocalDate date;
    private Long count;
  }
}
