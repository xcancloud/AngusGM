package cloud.xcan.angus.core.gm.domain;

public interface EventCoreMessage {

  String CHANNEL_TEST_MESSAGE = "xcm.gm.channel.test.message";

  String RECEIVE_SETTING_DELETE_IN_USE_KEY = "BEV010";
  String RECEIVE_SETTING_DELETE_IN_USE_T = "xcm.gm.setting.delete.in.use.t";

  String EVENT_PUSH_INVALID = "xcm.gm.push.is.invalid";
  String EVENT_PUSH_TEMPLATE_NOT_FOUND = "xcm.gm.push.template.not.found";
  String EVENT_PUSH_TEMPLATE_SETTING_NOT_FOUND_T = "xcm.gm.push.address.not.found.t";

}
