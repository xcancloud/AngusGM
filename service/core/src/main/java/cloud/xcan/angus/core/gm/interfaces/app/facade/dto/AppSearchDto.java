package cloud.xcan.angus.core.gm.interfaces.app.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.commonlink.app.AppType;
import cloud.xcan.angus.api.commonlink.app.OpenStage;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class AppSearchDto extends PageQuery {

  private Long id;

  private String code;

  private String name;

  private String showName;

  private String icon;

  private AppType type;

  private EditionType editionType;

  private Boolean authCtrl;

  private Boolean enabled;

  private String url;

  private Integer sequence;

  private String version;

  private OpenStage openStage;

  private String clientId;

  private Long apiId;

  private Long tagId;

  private Long createdBy;

  private Long tenantId;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
