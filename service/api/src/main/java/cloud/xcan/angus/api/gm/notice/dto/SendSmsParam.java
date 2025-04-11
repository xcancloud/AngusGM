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
  @Schema(description = "Out business id, used to establish associations with external businesses.",
      maxLength = MAX_OUT_ID_LENGTH)
  private String outId;

  @NotNull
  @Schema(description = "Sms business key.", example = "SIGNUP", requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @Schema(description = "Whether or not urgent sent flag.")
  private Boolean urgent = false;

  @Length(max = MAX_LANGUAGE_LENGTH)
  @Schema(description = "Use language to send sms content.", defaultValue = "zh_CN",
      example = "zh_CN", maxLength = MAX_LANGUAGE_LENGTH)
  private String language;

  @Size(max = MAX_MOBILE_SIZE)
  @Schema(description = "Receive SMS mobiles, maximum support for `500`. the mobiles and receiveObjectIds must have one of the parameters.")
  private List<String> mobiles;

  @Size(max = MAX_SMS_TEMPLATE_PARAM)
  @Schema(description = "Sms template parameters, maximum support for `50`.")
  private Map<String, String> templateParams;

  @Schema(description =
      "Sms recipient object type. The receiveObjectType is required when sending via receiveObjectIds, "
          + "receiveObjectIds and mobile must have one of the parameters.")
  private ReceiveObjectType receiveObjectType;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Sms recipient object ids, maximum support for `500`. Parameter mobiles and receiveObjectIds are required to choose one, if both are passed, mobiles is used by default")
  private List<Long> receiveObjectIds;

  @Size(max = MAX_SEND_OBJECT_SIZE)
  @Schema(description = "Sms recipient policy codes, maximum support for `500`.")
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
