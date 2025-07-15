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
  @Schema(description = "Event template id", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Schema(description =
      "Template event receiver types. At least one of receiverTypes and receiverIds is required, "
          + "event notification will also be sent when both exist")
  private Set<ReceiverType> receiverTypes;

  @Size(max = MAX_BATCH_SIZE)
  @Schema(description =
      "Template event receiver ids. At least one of receiverTypes and receiverIds is required, "
          + "event notification will also be sent when both exist. Supports a maximum of `200` recipients")
  private Set<Long> receiverIds;

  @Schema(description = "Event notification method types")
  private Set<NoticeType> noticeTypes;

}
