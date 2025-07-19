package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@Accessors(chain = true)
public class MessageCenterOnlineFindDto extends PageQuery {

  private Long userId;

  private String fullName;

  private String deviceId;

  private String remoteAddress;

  private Boolean online;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime onlineDate;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime offlineDate;

  @Override
  public String getDefaultOrderBy() {
    return "id";
  }
}
