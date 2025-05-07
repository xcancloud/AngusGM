package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.commonlink.app.func.AppFuncType;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiInfoVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagInfoVo;
import cloud.xcan.angus.remote.NameJoinField;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@Accessors(chain = true)
public class AppFuncDetailVo {

  private Long id;

  private String code;

  private String name;

  private String showName;

  private Long pid;

  private String icon;

  private AppFuncType type;

  private String description;

  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  private Long appId;

  private Integer sequence;

  private List<ApiInfoVo> apis;

  private List<AppTagInfoVo> tags;

  private String clientId;

  private String clientName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String createdByName;

  private Long tenantId;

  //@NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  //private String tenantName;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
