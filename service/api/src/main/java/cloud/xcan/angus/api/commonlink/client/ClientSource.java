package cloud.xcan.angus.api.commonlink.client;

import static java.util.Objects.nonNull;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import java.util.Map;
import lombok.Getter;

@Getter
@EndpointRegister
public enum ClientSource implements EnumMessage<String> {

  XCAN_TP_SIGNIN,
  XCAN_OP_SIGNIN,
  XCAN_SYS_INTROSPECT, // for /oauth2/introspect
  XCAN_SYS_TOKEN,
  XCAN_2P_SIGNIN;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isSystemToken() {
    return XCAN_SYS_TOKEN.equals(this);
  }

  public static boolean isUserSignIn(String clientSource) {
    return nonNull(clientSource) && (clientSource.equals(XCAN_TP_SIGNIN.getValue())
        || clientSource.equals(XCAN_OP_SIGNIN.getValue())
        /*|| clientSource.equals(XCAN_2P_SIGNIN.getValue()) -> System sign in */);
  }

  public static boolean isOperationClientSignIn(String clientSource) {
    return nonNull(clientSource) && clientSource.equals(XCAN_OP_SIGNIN.getValue());
  }

}
