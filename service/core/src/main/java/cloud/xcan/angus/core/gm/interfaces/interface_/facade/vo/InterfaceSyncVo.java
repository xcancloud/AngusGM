package cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface sync response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceSyncVo implements Serializable {

  private String serviceName;

  private LocalDateTime syncTime;

  private Integer totalInterfaces;

  private Integer newInterfaces;

  private Integer updatedInterfaces;

  private Integer deprecatedInterfaces;

  private List<ServiceSyncInfo> services;

  @Getter
  @Setter
  @Accessors(chain = true)
  public static class ServiceSyncInfo implements Serializable {
    private String serviceName;
    private Integer interfaceCount;
  }
}
