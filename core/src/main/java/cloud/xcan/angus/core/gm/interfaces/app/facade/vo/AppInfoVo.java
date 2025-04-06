package cloud.xcan.angus.core.gm.interfaces.app.facade.vo;

import cloud.xcan.angus.api.commonlink.app.AppType;
import cloud.xcan.angus.api.commonlink.app.OpenStage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AppInfoVo {

  private Long id;

  private String code;

  private String name;

  private String showName;

  private String icon;

  private AppType type;

  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  private Integer sequence;

  private String version;

  private OpenStage openStage;

}
