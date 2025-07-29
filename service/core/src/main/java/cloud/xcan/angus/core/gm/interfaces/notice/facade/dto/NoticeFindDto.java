package cloud.xcan.angus.core.gm.interfaces.notice.facade.dto;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.notice.NoticeScope;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoticeFindDto extends PageQuery {

  @Schema(description = "Notification identifier")
  private Long id;

  @Schema(description = "Notification content message")
  private String content;

  @Schema(description = "Notification scope for targeting recipients")
  private NoticeScope scope;

  @Schema(description = "Application identifier for APP scope notifications")
  private Long appId;

  @Schema(description = "Application unique code")
  private String appCode;

  @Schema(description = "Edition type classification")
  private EditionType editionType;

  @Schema(description = "Notification delivery timing")
  private SentType sendType;

  @Schema(description = "Scheduled delivery timestamp")
  private LocalDateTime timingDate;

}
