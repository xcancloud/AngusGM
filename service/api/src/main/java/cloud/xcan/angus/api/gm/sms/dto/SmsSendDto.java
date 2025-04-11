package cloud.xcan.angus.api.gm.sms.dto;

import static cloud.xcan.angus.api.commonlink.SmsConstants.DEFAULT_VC_VALID_SECOND;
import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_MOBILE_SIZE;
import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_SEND_OBJECT_SIZE;
import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_SMS_TEMPLATE_PARAM;
import static cloud.xcan.angus.api.commonlink.SmsConstants.MAX_VC_VALID_MINUTE;
import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LANGUAGE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_OUT_ID_LENGTH;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class SmsSendDto implements Serializable {

  @Length(max = MAX_OUT_ID_LENGTH)
  @Schema(description = "Out business id, used to establish associations with external businesses.",
      maxLength = MAX_OUT_ID_LENGTH)
  private String outId;

  @NotNull
  @Schema(description = "Sms business key.", example = "SIGNUP", requiredMode = RequiredMode.REQUIRED)
  private SmsBizKey bizKey;

  @Schema(description = "Whether or not urgent sent flag.", defaultValue = "false")
  private Boolean urgent = false;

  @DoInFuture("Support SMS in other regions")
  @Length(max = MAX_LANGUAGE_LENGTH)
  @Schema(description = "Use language to send sms content.", defaultValue = "zh_CN",
      example = "zh_CN", maxLength = MAX_LANGUAGE_LENGTH)
  private SupportedLanguage language = SupportedLanguage.zh_CN;

  @Size(max = MAX_MOBILE_SIZE)
  @Schema(description = "Receive SMS mobiles, maximum support for `500`. the mobiles and receiveObjectIds must have one of the parameters.")
  private List<String> mobiles;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Expected SMS sending time.")
  private LocalDateTime expectedSendDate;

  @NotNull
  @Schema(description = "Whether or not verification code sms flag.", defaultValue = "false", requiredMode = RequiredMode.REQUIRED)
  private Boolean verificationCode = false;

  @Max(MAX_VC_VALID_MINUTE)
  @Schema(description = "Validity period of verification code, in seconds. It is required when verificationCode=true.", defaultValue = "300")
  private Integer verificationCodeValidSecond = DEFAULT_VC_VALID_SECOND;

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

}
