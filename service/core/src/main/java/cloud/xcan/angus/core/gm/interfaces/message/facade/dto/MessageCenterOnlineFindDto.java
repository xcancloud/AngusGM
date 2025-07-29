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

  @Schema(description = "User identifier for online status search")
  private Long userId;

  @Schema(description = "User full name for filtering")
  private String fullName;

  @Schema(description = "Device identifier for online status tracking")
  private String deviceId;

  @Schema(description = "Remote IP address of the user")
  private String remoteAddress;

  @Schema(description = "Online status filter (true for online, false for offline)")
  private Boolean online;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "User online timestamp")
  private LocalDateTime onlineDate;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "User offline timestamp")
  private LocalDateTime offlineDate;

  @Override
  public String getDefaultOrderBy() {
    return "id";
  }
}
