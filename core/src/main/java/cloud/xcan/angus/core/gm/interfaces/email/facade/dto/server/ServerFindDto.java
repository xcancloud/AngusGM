package cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_HOST_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.remote.PageQuery;
import cloud.xcan.angus.validator.Port;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class ServerFindDto extends PageQuery {

  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  private String name;

  private EmailProtocol protocol;

  @Length(max = MAX_HOST_LENGTH)
  private String host;

  @Port
  private Integer port;

}
