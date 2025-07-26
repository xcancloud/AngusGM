package cloud.xcan.angus.core.gm.interfaces.notice.facade.dto;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.notice.NoticeScope;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoticeFindDto extends PageQuery {

  private Long id;

  private String content;

  private NoticeScope scope;

  private Long appId;

  private String appCode;

  private EditionType editionType;

  private SentType sendType;

  private LocalDateTime timingDate;

}
