package cloud.xcan.angus.core.gm.domain.event;

import static cloud.xcan.angus.api.commonlink.EventConstant.RECEIVE_CHANNEL_DING_TALK_QUOTA;
import static cloud.xcan.angus.api.commonlink.EventConstant.RECEIVE_CHANNEL_EMAIL_QUOTA;
import static cloud.xcan.angus.api.commonlink.EventConstant.RECEIVE_CHANNEL_WEBHOOK_QUOTA;
import static cloud.xcan.angus.api.commonlink.EventConstant.RECEIVE_CHANNEL_WECHAT_QUOTA;

import cloud.xcan.angus.spec.experimental.EndpointRegister;
import cloud.xcan.angus.spec.locale.EnumMessage;
import lombok.Getter;


@Getter
@EndpointRegister
public enum ReceiveChannelType implements EnumMessage<String> {

  WEBHOOK(RECEIVE_CHANNEL_WEBHOOK_QUOTA),
  EMAIL(RECEIVE_CHANNEL_EMAIL_QUOTA),
  DINGTALK(RECEIVE_CHANNEL_DING_TALK_QUOTA),
  WECHAT(RECEIVE_CHANNEL_WECHAT_QUOTA);

  final int quota;

  ReceiveChannelType(int quota) {
    this.quota = quota;
  }

  @Override
  public String getValue() {
    return this.name();
  }

}
