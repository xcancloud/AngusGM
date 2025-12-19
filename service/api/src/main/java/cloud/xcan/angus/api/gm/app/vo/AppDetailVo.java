package cloud.xcan.angus.api.gm.app.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.OpenStage;
import cloud.xcan.angus.remote.MessageJoinField;
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

  @MessageJoinField(type = "APP")
  private String name;

  @MessageJoinField(type = "APP")
  private String showName;

  private String icon;

  private AppType type;

  private EditionType editionType;

  @MessageJoinField(type = "APP")
  private String description;

  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  private Integer sequence;

  private String version;

  private OpenStage openStage;

  private String clientId;

  private String clientName;

  private Long createdBy;

  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creator;

  private Long tenantId;

  @NameJoinField(id = "tenantId", repository = "commonTenantRepo")
  private String tenantName;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

  private List<ApiInfoVo> apis;

  private List<AppTagVo> tags;

}
