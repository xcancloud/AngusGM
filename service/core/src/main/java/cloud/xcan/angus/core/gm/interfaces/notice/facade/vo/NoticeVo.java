package cloud.xcan.angus.core.gm.interfaces.notice.facade.vo;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.notice.NoticeScope;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class NoticeVo implements Serializable {

  private Long id;

  private String content;

  private NoticeScope scope;

  private Long appId;

  private String appCode;

  private String appName;

  private EditionType editionType;

  private SentType sendType;

  private LocalDateTime timingDate;

  private LocalDateTime expirationDate;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private LocalDateTime createdDate;

}
