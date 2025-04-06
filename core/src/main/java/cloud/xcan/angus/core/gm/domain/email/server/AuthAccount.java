package cloud.xcan.angus.core.gm.domain.email.server;


import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class AuthAccount extends ValueObjectSupport<AuthAccount> {

  private String account;

  private String password;

}
