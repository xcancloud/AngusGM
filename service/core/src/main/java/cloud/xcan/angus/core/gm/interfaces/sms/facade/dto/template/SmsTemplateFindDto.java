package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template;

import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SmsTemplateFindDto extends PageQuery {

  @Schema(description = "SMS template unique identifier for filtering")
  private Long id;

  @Schema(description = "SMS template identifier code for searching")
  private String code;

  @Schema(description = "SMS template display name for searching")
  private String name;

  @Schema(description = "Third-party SMS template identifier code for filtering")
  private String thirdCode;

  @Schema(description = "SMS template language for filtering")
  private SupportedLanguage language;

  @Schema(description = "SMS channel identifier for filtering templates by channel")
  private Long channelId;

  @Override
  public String getDefaultOrderBy() {
    return "id";
  }

  @Override
  public OrderSort getDefaultOrderSort() {
    return OrderSort.ASC;
  }

}
