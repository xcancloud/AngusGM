package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class TenantQuotaFindDto extends PageQuery {

  private Long id;

  private String appCode;

  @Schema(hidden = true)
  private String serviceCode;

  private String name;

  private Boolean allowChange;

}
