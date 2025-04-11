package cloud.xcan.angus.core.gm.interfaces.email.facade.vo.server;

import cloud.xcan.angus.core.gm.domain.email.server.EmailProtocol;
import cloud.xcan.angus.core.gm.interfaces.email.facade.to.AuthAccountTo;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ServerDetailVo implements Serializable {

  private Long id;

  private String name;

  private EmailProtocol protocol;

  private String remark;

  private Boolean enabled;

  private String host;

  private Integer port;

  private Boolean startTlsEnabled;

  private Boolean sslEnabled;

  private Boolean authEnabled;

  private AuthAccountTo authAccount;

  private String subjectPrefix;

}
