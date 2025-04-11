package cloud.xcan.angus.extension.sms.plugin;

import static cloud.xcan.angus.spec.experimental.SimpleResult.SUCCESS_CODE;

import cloud.xcan.angus.extension.sms.api.MessageChannel;
import cloud.xcan.angus.extension.sms.api.Sms;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import cloud.xcan.angus.plugin.api.Extension;
import cloud.xcan.angus.spec.experimental.SimpleResult;
import com.google.gson.Gson;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Extension
public class HuaweicloudSmsProvider implements SmsProvider {

  private final Gson GSON;

  public HuaweicloudSmsProvider() {
    this.GSON = new Gson();
  }

  /**
   * Maximum 500 SMS messages be distributed by business
   */
  @Override
  public SimpleResult sendSms(Sms sms, MessageChannel channel) {
    try {
      String result = HuaweiSmsSender
          .send(channel.getEndpoint(), channel.getAccessKeyId(),
              channel.getAccessKeySecret(), channel.getThirdChannelNo(),
              sms.getTemplateCode(), sms.getSign(),
              // Up to 500 mobile numbers are supported
              // @see https://support.huaweicloud.com/api-msgsms/sms_05_0001.html
              StringUtils.join(sms.getMobiles(), ","),
              GSON.toJson(sms.getTemplateParams().values()));
      Map<?, ?> resultMap = GSON.fromJson(result, Map.class);
      return new SimpleResult()
          .setCode("000000".equals(String.valueOf(resultMap.get("code"))) ? SUCCESS_CODE
              : String.valueOf(resultMap.get("code")))
          .setMessage(String.valueOf(resultMap.get("description")));
    } catch (Exception e) {
      e.printStackTrace();
      return new SimpleResult().setCode("exception").setMessage(e.getMessage());
    }
  }

  /**
   * Private version requires tenants to configure their own SMS channel information
   */
  @Override
  public MessageChannel getInstallationChannel() {
    return new MessageChannel("HuaweiCloud SMS",
        "https://res.hc-cdn.com/cnpm-header-and-footer/2.0.6/base/header-china/components/images/logo.svg",
        System.getProperty("SMS_HUAWEICLOUD_ENDPOINT"),
        System.getProperty("SMS_HUAWEICLOUD_AK"),
        System.getProperty("SMS_HUAWEICLOUD_SK"),
        "ismsapp0000000012"
    );
  }

}
