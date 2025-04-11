package cloud.xcan.angus.api.gm.notice.dto;

import cloud.xcan.angus.api.enums.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SendNoticeDto implements Serializable {

  @NotNull
  @Size(min = 1, max = 3)
  @Schema(description = "Notification method type", requiredMode = RequiredMode.REQUIRED)
  private List<NoticeType> noticeTypes;

  @Valid
  @Schema(description = "Required when NoticeType is SMS")
  private SendSmsParam sendSmsParam;

  @Valid
  @Schema(description = "Required when NoticeType is EMAIL")
  private SendEmailParam sendEmailParam;

  @Valid
  @Schema(description = "Required when NoticeType is IN_SITE")
  private SendInsiteParam sendInsiteParam;

}


