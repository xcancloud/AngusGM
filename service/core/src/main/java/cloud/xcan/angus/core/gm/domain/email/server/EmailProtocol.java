package cloud.xcan.angus.core.gm.domain.email.server;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumValueMessage;


@EndpointRegister
public enum EmailProtocol implements EnumValueMessage<String> {

  SMTP, POP3, IMAP;

  @Override
  public String getValue() {
    return this.name();
  }

}
