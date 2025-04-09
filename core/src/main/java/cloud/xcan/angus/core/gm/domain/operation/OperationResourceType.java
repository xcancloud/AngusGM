package cloud.xcan.angus.core.gm.domain.operation;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;

@EndpointRegister
public enum OperationResourceType implements EnumMessage<String> {
  // @formatter:off
  TENANT(false), DEPT(true), GROUP(true), USER(true), USER_DIRECTORY(true), USER_TOKEN(true), ORG_TAG(true),
  AUTH_CLIENT(true), AUTH_USER(true),
  SERVICE(false), API(false), APP(false), APP_FUNC(false), WEB_TAG(false),
  POLICY(true), POLICY_APP(true), POLICY_FUNC(true), POLICY_TENANT(true), POLICY_DEPT(true), POLICY_GROUP(true), POLICY_USER(true),
  EVENT(true), EVENT_CHANNEL(true), EVENT_TEMPLATE(true),
  EMAIL(true), EMAIL_SERVER(true), EMAIL_TEMPLATE(true),
  SMS(true), SMS_CHANNEL(true), SMS_TEMPLATE(true),
  TO_ROLE(false), TO_USER(false),
  MESSAGE(true), NOTICE(true),
  SETTING(true), SETTING_USER(true), SETTING_TENANT(true), SETTING_TENANT_QUOTA(true),
  SYSTEM_TOKEN(true),
  OTHER(true);
  // @formatter:on

  private final boolean private0;

  OperationResourceType(boolean private0) {
    this.private0 = private0;
  }

  public boolean isPrivate0() {
    return private0;
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
