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

  private Long id;

  private Boolean read;

  private Long messageId;

  @JsonIgnore
  @Schema(hidden = true)
  private transient Long receiveUserId;

  private Boolean delete;

  private LocalDateTime createDate;

}
