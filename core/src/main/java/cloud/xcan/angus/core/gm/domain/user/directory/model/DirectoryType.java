package cloud.xcan.angus.core.gm.domain.user.directory.model;

import cloud.xcan.angus.spec.ValueObject;
import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumValueMessage;
import lombok.Getter;


@EndpointRegister
@Getter
public enum DirectoryType implements ValueObject<DirectoryType>, EnumValueMessage<String> {
  MicrosoftActiveDirectory("Microsoft Active Directory"),
  ApacheDS("Apache Directory Server"),
  AppleOpenDirectory("Apple Open Directory"),
  FedoraDS("FedoraDS"),
  GenericLDAP("Generic Directory Server"),
  NovelleDirectory("Novell eDirectory Server"),
  OpenDS("OpenDS"),
  OpenLDAP("OpenLDAP"),
  SunDirectory("Sun Directory Server");

  final String message;

  DirectoryType(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
