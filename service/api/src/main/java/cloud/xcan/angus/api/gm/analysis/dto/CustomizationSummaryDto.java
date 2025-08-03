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

  @Schema(description = "Unique identifier for the summary project configuration. Used to associate summary settings with specific projects for data analysis and reporting")
  private Long projectId;

  @Schema(description = "Multi-tenant control flag. When true, disables multi-tenant control and summarizes all tenants. When false, applies tenant-specific filtering for data isolation", example = "false")
  private boolean closeMultiTenantCtrl = false;

  @NotEmpty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Required name for the summary configuration. Must not exceed maximum name length constraint. Used for identifying and managing summary configurations", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "Defines the grouping strategy for summary data. Determines how data is grouped for aggregation and analysis")
  private GroupBy groupBy;

  @Size(max = 5)
  @Schema(description = "List of columns used for grouping statistics. Each column represents a dimension for data aggregation")
  private List<String> groupByColumns;

  @Schema(description = "Defines the supported date statistics range for time-based data grouping and analysis")
  private DateRangeType dateRangeType;

  @Valid
  @Size(max = 10)
  @Schema(description = "List of aggregate functions and column configurations")
  private List<Aggregate> aggregates;

  @Size(max = MAX_FILTER_SIZE)
  @Schema(description = "Dynamic filter and search conditions for filtering summary data")
  protected List<SearchCriteria> filters = new ArrayList<>();

}
