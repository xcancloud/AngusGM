package cloud.xcan.angus.api.gm.analysis.dto;

import static cloud.xcan.angus.remote.ApiConstant.RLimit.MAX_FILTER_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.core.jpa.repository.summary.Aggregate;
import cloud.xcan.angus.core.jpa.repository.summary.DateRangeType;
import cloud.xcan.angus.core.jpa.repository.summary.GroupBy;
import cloud.xcan.angus.remote.search.SearchCriteria;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class CustomizationSummaryDto implements Serializable {

  @Schema(description = "Summary project id.")
  private Long projectId;

  @Schema(description = "Turn off multi tenant control, summary all tenants when true.", example = "false")
  private boolean closeMultiTenantCtrl = false;

  @NotEmpty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Summary resource name.", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "Summary group type.")
  private GroupBy groupBy;

  @Size(max = 5)
  @Schema(description = "Grouping statistics column count, supporting a maximum of `5` columns.")
  private List<String> groupByColumns;

  @Schema(description = "Supported date statistics range.")
  private DateRangeType dateRangeType;

  @Valid
  @Size(max = 10)
  @Schema(description = "Aggregate function and column configuration, supporting a maximum of `10` columns.")
  private List<Aggregate> aggregates;

  @Size(max = MAX_FILTER_SIZE)
  @Schema(description = "Dynamic filter and search conditions, max " + MAX_FILTER_SIZE)
  protected List<SearchCriteria> filters = new ArrayList<>();

}
