package cloud.xcan.angus.core.gm.domain;

public interface EventCoreMessage {

  String CHANNEL_TEST_MESSAGE = "xcm.angusgm.channel.test.message";

  String RECEIVE_SETTING_DELETE_IN_USE_KEY = "BEV010";
  String RECEIVE_SETTING_DELETE_IN_USE_T = "xcm.angusgm.setting.delete.in.use.t";

  String EVENT_PUSH_INVALID = "xcm.angusgm.push.is.invalid";
  String EVENT_PUSH_TEMPLATE_NOT_FOUND = "xcm.angusgm.push.template.not.found";
  String EVENT_PUSH_TEMPLATE_SETTING_NOT_FOUND_T = "xcm.angusgm.push.address.not.found.t";

}
