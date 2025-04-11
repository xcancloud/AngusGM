package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import cloud.xcan.angus.core.gm.domain.message.MessageStatus;
import cloud.xcan.angus.remote.NameJoinField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MessageVo implements Serializable {

  private Long id;

  private String title;

  private MessageReceiveType receiveType;

  private SentType sentType;

  private MessageStatus status;

  private String failureReason;

  private Integer sentNum;

  private Integer readNum;

  private LocalDateTime timingDate;

  private ReceiveObjectType receiveObjectType;

  private Long receiveTenantId;

  @NameJoinField(id = "receiveTenantId", repository = "commonTenantRepo")
  private String receiveTenantName;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;
}
