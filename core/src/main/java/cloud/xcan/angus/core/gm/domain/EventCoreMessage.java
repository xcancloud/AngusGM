package cloud.xcan.angus.core.gm.domain;

public interface EventCoreMessage {

  String CHANNEL_TEST_MESSAGE = "xcm.event.channel.test.message";

  String RECEIVE_SETTING_DELETE_IN_USE_KEY = "BEV010";
  String RECEIVE_SETTING_DELETE_IN_USE_T = "xcm.event.setting.delete.in.use.t";

  String EVENT_PUSH_INVALID = "xcm.event.push.is.invalid";
  String EVENT_PUSH_TEMPLATE_NOT_FOUND = "xcm.event.push.template.not.found";
  String EVENT_PUSH_TEMPLATE_SETTING_NOT_FOUND_T = "xcm.event.push.address.not.found.t";

}
