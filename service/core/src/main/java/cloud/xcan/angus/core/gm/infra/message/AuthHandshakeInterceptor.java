package cloud.xcan.angus.core.gm.infra.message;


import static cloud.xcan.angus.core.spring.security.TokenValidator.validate;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.PRINCIPAL;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class AuthHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    Map<String, Object> principal = validate(request);
    attributes.put(PRINCIPAL, principal);
    return super.beforeHandshake(request, response, wsHandler, attributes);
  }

}
