package cloud.xcan.angus.api.commonlink;

public interface EventConstant {

  int MAX_EVENT_RESULT_MSG_LENGTH = 200;

  /**
   * Event duplicate.
   */
  String EVENT_DUPLICATE_REDIS_KEY = "gm:event:duplicate:";
  int EVENT_DUPLICATE_MAX_KEY_LENGTH = 200;
  Long EVENT_DUPLICATE_KEY_EXPIRE = 5L;

  int RECEIVE_CHANNEL_WEBHOOK_QUOTA = 5;
  int RECEIVE_CHANNEL_EMAIL_QUOTA = 20;
  int RECEIVE_CHANNEL_DING_TALK_QUOTA = 10;
  int RECEIVE_CHANNEL_WECHAT_QUOTA = 10;

}
