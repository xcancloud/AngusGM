package cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.enums.ReceiverType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EventTemplateReceiverDto {

  @NotNull
  @Schema(description = "Event template identifier", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Schema(description = "Receiver type categories for event notifications. At least one of receiverTypes or receiverIds required")
  private Set<ReceiverType> receiverTypes;

  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "Specific receiver identifiers for event notifications. At least one of receiverTypes or receiverIds required")
  private Set<Long> receiverIds;

  @Schema(description = "Notification delivery method types")
  private Set<NoticeType> noticeTypes;

}
