package cloud.xcan.angus.core.gm.interfaces.app.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.commonlink.app.AppType;
import cloud.xcan.angus.api.commonlink.app.OpenStage;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiInfoVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagVo;
import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Accessors(chain = true)
public class AppDetailVo {

  private Long id;

  private String code;

  private String name;

  private String showName;

  private String icon;

  private AppType type;

  private EditionType editionType;

  private String description;

  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  private Integer sequence;

  private List<ApiInfoVo> apis;

  private List<AppTagVo> tags;

  private String version;

  private OpenStage openStage;

  private String clientId;

  @NameJoinField(id = "clientId", repository = "clientRepo")
  private String clientName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantName;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
