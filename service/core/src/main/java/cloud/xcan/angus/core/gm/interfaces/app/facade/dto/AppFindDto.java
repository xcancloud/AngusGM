package cloud.xcan.angus.core.gm.interfaces.app.facade.dto;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DATE_FMT;

import cloud.xcan.angus.api.enums.AppType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.enums.OpenStage;
import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Accessors(chain = true)
public class AppFindDto extends PageQuery {

  @Schema(description = "Application identifier for filtering")
  private Long id;

  @Schema(description = "Application code for filtering")
  private String code;

  @Schema(description = "Application display name for filtering")
  private String name;

  @Schema(description = "Application short display name for filtering")
  private String showName;

  @Schema(description = "Application icon URL for filtering")
  private String icon;

  @Schema(description = "Application type for filtering")
  private AppType type;

  @Schema(description = "Application edition type for filtering")
  private EditionType editionType;

  @Schema(description = "Authorization control status for filtering")
  private Boolean authCtrl;

  @Schema(description = "Application enabled status for filtering")
  private Boolean enabled;

  @Schema(description = "Application access URL for filtering")
  private String url;

  @Schema(description = "Application display sequence order for filtering")
  private Integer sequence;

  @Schema(description = "Application software version for filtering")
  private String version;

  @Schema(description = "Application activation stage for filtering")
  private OpenStage openStage;

  @Schema(description = "OAuth2 client identifier for filtering")
  private String clientId;

  @Schema(description = "Associated API identifier for filtering")
  private Long apiId;

  @Schema(description = "Application tag identifier for filtering")
  private Long tagId;

  @Schema(description = "Application creator identifier for filtering")
  private Long createdBy;

  @Schema(description = "Tenant identifier for filtering")
  private Long tenantId;

  @DateTimeFormat(pattern = DATE_FMT)
  @Schema(description = "Application creation date for filtering")
  private LocalDateTime createdDate;

  @Override
  @JsonIgnore
  @Schema(hidden = true)
  public String getDefaultOrderBy() {
    return "sequence";
  }

  @Override
  @JsonIgnore
  @Schema(hidden = true)
  public OrderSort getDefaultOrderSort() {
    return OrderSort.ASC;
  }

}
