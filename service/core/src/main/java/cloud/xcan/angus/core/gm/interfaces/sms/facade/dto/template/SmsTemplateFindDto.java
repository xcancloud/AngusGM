package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template;

import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SmsTemplateFindDto extends PageQuery {

  private Long id;

  private String code;

  private String name;

  private String thirdCode;

  private SupportedLanguage language;

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
