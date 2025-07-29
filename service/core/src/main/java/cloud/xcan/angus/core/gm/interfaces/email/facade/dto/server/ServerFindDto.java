package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server;

import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ServerFindDto extends PageQuery {

  @Schema(description = "Email server identifier")
  private Long id;

  @Schema(description = "Email server name for filtering")
  private String name;

  @Schema(description = "Email server protocol type for filtering")
  private EmailProtocol protocol;

}
