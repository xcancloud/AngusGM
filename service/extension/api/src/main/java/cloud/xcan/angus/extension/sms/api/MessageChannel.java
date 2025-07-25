package cloud.xcan.angus.extension.sms.api;

import java.io.Serializable;

/**
 * SMS service provider information.
 *
 * @author XiaoLong Liu
 */
public class MessageChannel implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * SMS service provider name.
   */
  private String name;

  /**
   * SMS service provider logo.
   */
  private String logo;

  String endpoint;

  String accessKeyId;

  String accessKeySecret;

  /**
   * Third SMS channel no. such as: huaweiyun sender.
   */
  String thirdChannelNo;

  public MessageChannel() {
  }

  public MessageChannel(String name, String logo, String endpoint, String accessKeyId,
      String accessKeySecret, String thirdChannelNo) {
    this.name = name;
    this.logo = logo;
    this.endpoint = endpoint;
    this.accessKeyId = accessKeyId;
    this.accessKeySecret = accessKeySecret;
    this.thirdChannelNo = thirdChannelNo;
  }

  public String getLogo() {
    return logo;
  }

  public MessageChannel setLogo(String logo) {
    this.logo = logo;
    return this;
  }

  public String getName() {
    return name;
  }

  public MessageChannel setName(String name) {
    this.name = name;
    return this;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public MessageChannel setEndpoint(String endpoint) {
    this.endpoint = endpoint;
    return this;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public MessageChannel setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
    return this;
  }

  public String getAccessKeySecret() {
    return accessKeySecret;
  }

  public MessageChannel setAccessKeySecret(String accessKeySecret) {
    this.accessKeySecret = accessKeySecret;
    return this;
  }

  public String getThirdChannelNo() {
    return thirdChannelNo;
  }

  public MessageChannel setThirdChannelNo(String thirdChannelNo) {
    this.thirdChannelNo = thirdChannelNo;
    return this;
  }
}
