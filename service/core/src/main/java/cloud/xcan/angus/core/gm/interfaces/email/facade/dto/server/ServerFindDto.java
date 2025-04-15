package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server;

import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.remote.PageQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ServerFindDto extends PageQuery {

  private Long id;

  private String name;

  private EmailProtocol protocol;

  private String host;

  private Integer port;

}
