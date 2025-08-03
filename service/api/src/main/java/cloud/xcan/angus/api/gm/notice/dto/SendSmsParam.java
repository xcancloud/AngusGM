package cloud.xcan.angus.api.gm.notice.dto;

import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_MOBILE_SIZE;
import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_SEND_OBJECT_SIZE;
import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_SMS_TEMPLATE_PARAM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LANGUAGE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OUT_ID_LENGTH;

import cloud.xcan.angus.api.commonlink.SmsConstants;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@ToString
public class SendSmsParam implements Serializable {

  @Length(max = MAX_OUT_ID_LENGTH)
  @Schema(description = "External business identifier for establishing associations with external systems. Used for cross-system integration and tracking")
  private String outId;

  @NotNull
  @Schema(description = "SMS business key for template-based SMS sending. Used for identifying the SMS template and context", example = "SIGNUP", requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @Schema(description = "Flag indicating urgent SMS priority. Used for priority-based SMS processing and delivery")
  private Boolean urgent = false;

  @Length(max = MAX_LANGUAGE_LENGTH)
  @Schema(description = "Language code for SMS content localization. Used for multi-language SMS template selection", defaultValue = "zh_CN", example = "zh_CN")
  private String language;

  @Size(max = MAX_MOBILE_SIZE)
  @Schema(description = "List of recipient mobile numbers for SMS delivery. Takes precedence over receiveObjectIds when both are provided")
  private List<String> mobiles;

  @Size(max = MAX_SMS_TEMPLATE_PARAM)
  @Schema(description = "SMS template parameters for template-based sending. Supports maximum of 50 parameters for comprehensive template customization")
  private Map<String, String> templateParams;

  @Schema(description = "SMS recipient object type for system-based recipient selection. Required when using receiveObjectIds for recipient identification. Secondary to mobiles when both are provided")
  private ReceiveObjectType receiveObjectType;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "SMS recipient object identifiers for system-based recipient selection. Secondary to mobiles when both are provided")
  private List<Long> receiveObjectIds;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "SMS recipient policy codes for policy-based recipient selection")
  private List<String> receivePolicyCodes;

  public SendSmsParam(Builder builder) {
    setOutId(builder.outId);
    setBizKey(builder.bizKey);
    setLanguage(builder.language);
    setUrgent(builder.urgent);
    setMobiles(builder.mobiles);
    setTemplateParams(builder.templateParams);
    setReceiveObjectType(builder.receiveObjectType);
    setReceiveObjectIds(builder.receiveObjectIds);
    setReceivePolicyCodes(builder.receivePolicyCodes);
  }

  public SendSmsParam() {
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    // @formatter:off
    private @Length(max = MAX_OUT_ID_LENGTH) String outId;
    private @NotNull SmsBizKey bizKey;
    private Boolean urgent = false;
    private @Length(max = MAX_LANGUAGE_LENGTH) String language;
    private @NotNull @Size(max = MAX_MOBILE_SIZE) List<String> mobiles;
    private @Size(max = MAX_SMS_TEMPLATE_PARAM) Map<String, String> templateParams;
    private ReceiveObjectType receiveObjectType;
    private @Size(max = MAX_SEND_OBJECT_SIZE) List<Long> receiveObjectIds;
    private @Size(max = MAX_SEND_OBJECT_SIZE) List<String> receivePolicyCodes;
    // @formatter:on

    private Builder() {
    }

    public Builder outId(String outId) {
      this.outId = outId;
      return this;
    }

    public Builder bizKey(@NotNull SmsBizKey bizKey) {
      this.bizKey = bizKey;
      return this;
    }

    public Builder language(@Length(max = MAX_LANGUAGE_LENGTH) String language) {
      this.language = language;
      return this;
    }

    public Builder urgent(Boolean urgent) {
      this.urgent = urgent;
      return this;
    }

    public Builder mobiles(@NotNull @Size(max = MAX_MOBILE_SIZE) List<String> mobiles) {
      this.mobiles = mobiles;
      return this;
    }

    public Builder templateParams(@Size(max = MAX_SMS_TEMPLATE_PARAM)
    Map<String, String> templateParams) {
      this.templateParams = templateParams;
      return this;
    }


    public Builder receiveObjectType(ReceiveObjectType receiveObjectType) {
      this.receiveObjectType = receiveObjectType;
      return this;
    }

    public Builder receiveObjectIds(
        @Size(max = SmsConstants.MAX_SEND_OBJECT_SIZE) List<Long> receiveObjectIds) {
      this.receiveObjectIds = receiveObjectIds;
      return this;
    }

    public Builder receivePolicyCodes(
        @Size(max = SmsConstants.MAX_SEND_OBJECT_SIZE) List<String> receivePolicyCodes) {
      this.receivePolicyCodes = receivePolicyCodes;
      return this;
    }

    public SendSmsParam build() {
      return new SendSmsParam(this);
    }
  }
}
