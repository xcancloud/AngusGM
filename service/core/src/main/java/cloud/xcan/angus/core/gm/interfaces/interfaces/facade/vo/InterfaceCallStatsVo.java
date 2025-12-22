package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface call stats VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceCallStatsVo implements Serializable {

  private Long interfaceId;

  private String path;

  private String method;

  private Period period;

  private Long totalRequests;

  private Long successRequests;

  private Long failedRequests;

  private Integer avgResponseTime;

  private Integer maxResponseTime;

  private Integer minResponseTime;

  private List<DailyCount> requestsPerDay;

  private List<ErrorCode> errorCodes;

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

  @Getter
  @Setter
  @Accessors(chain = true)
  public static class ErrorCode implements Serializable {
    private Integer code;
    private Long count;
  }
}
