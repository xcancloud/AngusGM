package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantType;
import cloud.xcan.angus.api.enums.TenantSource;
import cloud.xcan.angus.api.enums.TenantStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppUnauthTenantVo {

  private Long id;

  private String name;

  private String no;

  private TenantType type;

  private TenantSource source;

  private TenantStatus status;

  private TenantRealNameStatus realNameStatus;

  private Boolean locked;

}
