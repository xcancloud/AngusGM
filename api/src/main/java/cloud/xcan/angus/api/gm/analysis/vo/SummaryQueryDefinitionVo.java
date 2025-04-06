package cloud.xcan.angus.api.gm.analysis.vo;

import cloud.xcan.angus.core.jpa.repository.summary.DateRangeType;
import cloud.xcan.angus.core.jpa.repository.summary.GroupBy;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class SummaryQueryDefinitionVo implements Serializable {

  private List<SummaryQueryVo> summaryQuery;

  private GroupBy[] groupBys = GroupBy.values();

  private DateRangeType[] dateRangeTypes = DateRangeType.values();

}
