package cloud.xcan.angus.core.gm.interfaces.app.facade.dto;

import static cloud.xcan.angus.api.commonlink.AASConstant.MAX_APP_FUNC_SHORT_NAME_LENGTH;
import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import cloud.xcan.angus.api.commonlink.app.AppType;
import cloud.xcan.angus.api.commonlink.app.OpenStage;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.remote.PageQuery;
import cloud.xcan.angus.validator.Version;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class AppSearchDto extends PageQuery {

  private Long id;

  @Length(max = MAX_CODE_LENGTH)
  private String code;

  @Length(max = MAX_NAME_LENGTH)
  private String name;

  @Length(max = MAX_APP_FUNC_SHORT_NAME_LENGTH)
  private String showName;

  @Length(max = MAX_URL_LENGTH)
  private String icon;

  private AppType type;

  private EditionType editionType;

  private Boolean authCtrl;

  private Boolean enabled;

  @Length(max = MAX_URL_LENGTH)
  private String url;

  @Positive
  private Integer sequence;

  @Version
  private String version;

  private OpenStage openStage;

  @Length(max = MAX_CODE_LENGTH)
  private String clientId;

  private Long apiId;

  private Long tagId;

  private Long createdBy;

  private Long tenantId;

  @DateTimeFormat(pattern = DATE_FMT)
  private LocalDateTime createdDate;

}
