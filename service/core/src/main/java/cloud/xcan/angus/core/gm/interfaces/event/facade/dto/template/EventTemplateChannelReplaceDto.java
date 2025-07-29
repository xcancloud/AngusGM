package cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class EventTemplateChannelReplaceDto implements Serializable {

  @NotNull
  @Schema(description = "Event template identifier", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Schema(description = "Event channel identifiers to assign. Empty to clear all channels")
  private Set<Long> channelIds;

}
