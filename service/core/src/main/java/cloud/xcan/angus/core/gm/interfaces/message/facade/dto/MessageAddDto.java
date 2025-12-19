package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import static cloud.xcan.angus.api.commonlink.MessageConstant.MAX_MESSAGE_LENGTH;
import static cloud.xcan.angus.api.commonlink.MessageConstant.MAX_RECEIVE_ORG_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.gm.domain.SentType;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import cloud.xcan.angus.core.gm.domain.message.ReceiveObject;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.validator.EnumPart;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
public class MessageAddDto implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Message title for display", requiredMode = RequiredMode.REQUIRED)
  private String title;

  @DoInFuture("Support rich text and editor")
  @NotBlank
  @Length(max = MAX_MESSAGE_LENGTH)
  @Schema(description = "Message content body", requiredMode = RequiredMode.REQUIRED)
  private String content;

  @NotNull
  @Schema(description = "Message reception type for delivery method", requiredMode = RequiredMode.REQUIRED)
  private MessageReceiveType receiveType;

  @NotNull
  @Schema(description = "Message delivery timing configuration", requiredMode = RequiredMode.REQUIRED)
  private SentType sendType;

  @Future
  @Schema(description = "Scheduled delivery timestamp for timed messages")
  private LocalDateTime timingDate;

  @Schema(description = "Target tenant identifier for message delivery")
  private Long receiveTenantId;

  @NotNull
  @EnumPart(enumClass = ReceiveObjectType.class, allowableValues = {"TENANT", "DEPT", "GROUP",
      "USER", "ALL"})
  @Schema(description = "Recipient object type for message targeting", allowableValues = {"TENANT",
      "DEPT",
      "GROUP", "USER", "ALL"}, requiredMode = RequiredMode.REQUIRED)
  private ReceiveObjectType receiveObjectType;

  @Valid
  @Size(max = MAX_RECEIVE_ORG_NUM)
  @Schema(description = "Recipient object list for targeted delivery. Empty when receiveObjectType=ALL")
  private List<ReceiveObject> receiveObjects;

}
