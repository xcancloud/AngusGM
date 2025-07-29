package cloud.xcan.angus.core.gm.interfaces.message.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageCurrentFindDto extends PageQuery {

  @Schema(description = "Message identifier")
  private Long id;

  @Schema(description = "Message read status filter")
  private Boolean read;

  @Schema(description = "Original message identifier")
  private Long messageId;

  @JsonIgnore
  @Schema(hidden = true)
  private transient Long receiveUserId;

  @Schema(description = "Message deletion status filter")
  private Boolean delete;

  @Schema(description = "Message creation timestamp")
  private LocalDateTime createDate;

}
