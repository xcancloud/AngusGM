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
  XCAN_USER_TOKEN,
  XCAN_SYS_TOKEN,
  XCAN_2P_SIGNIN;

  public static final Map<ClientSource, String> TOKEN_PREFIX_MAP = Map.of(
      XCAN_TP_SIGNIN, "TST.", XCAN_OP_SIGNIN, "OST.",
      XCAN_USER_TOKEN, "0UT.", XCAN_SYS_TOKEN, "0ST.",
      XCAN_2P_SIGNIN, "2PT."
  );

  @Override
  public String getValue() {
    return this.name();
  }

  public String getTokenPrefix() {
    return TOKEN_PREFIX_MAP.get(this);
  }

  public boolean isSystemToken() {
    return XCAN_SYS_TOKEN.equals(this);
  }

  public boolean isUserToken() {
    return XCAN_USER_TOKEN.equals(this);
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
