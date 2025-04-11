package cloud.xcan.angus.extension.sms.plugin;

import static cloud.xcan.angus.spec.experimental.SimpleResult.SUCCESS_CODE;

import cloud.xcan.angus.extension.sms.api.MessageChannel;
import cloud.xcan.angus.extension.sms.api.Sms;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import cloud.xcan.angus.plugin.api.Extension;
import cloud.xcan.angus.spec.experimental.SimpleResult;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

@Extension
public class AliyunSmsProvider implements SmsProvider {

  private final Gson gson;

  public AliyunSmsProvider() {
    this.gson = new Gson();
  }

  /**
   * Maximum 500 SMS messages be distributed by business.
   */
  @Override
  public SimpleResult sendSms(Sms sms, MessageChannel channel) {
    try {
      Config config = new Config().setAccessKeyId(channel.getAccessKeyId())
          .setAccessKeySecret(channel.getAccessKeySecret())
          .setEndpoint(channel.getEndpoint());
      SendSmsResponse response = new Client(config).sendSms(smsToSendSmsRequest(sms));
      return new SimpleResult()
          .setCode("OK".equals(response.body.getCode()) ? SUCCESS_CODE : response.body.getCode())
          .setMessage(response.body.getMessage());
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
    return new MessageChannel("Aliyun SMS",
        "https://img.alicdn.com/tfs/TB13DzOjXP7gK0jSZFjXXc5aXXa-212-48.png",
        System.getProperty("SMS_ALIYUN_ENDPOINT"),
        System.getProperty("SMS_ALIYUN_AK"),
        System.getProperty("SMS_ALIYUN_SK"),
        null
    );
  }

  private SendSmsRequest smsToSendSmsRequest(Sms sms) {
    return new SendSmsRequest().setSignName(sms.getSign())
        // Up to 1000 mobile numbers are supported
        // @see https://help.aliyun.com/document_detail/419273.htm?spm=a2c4g.11186623.0.0.3437507670xDw3
        .setPhoneNumbers(StringUtils.join(sms.getMobiles(), ","))
        .setTemplateCode(sms.getTemplateCode())
        .setTemplateParam(gson.toJson(sms.getTemplateParams()));
  }

}
