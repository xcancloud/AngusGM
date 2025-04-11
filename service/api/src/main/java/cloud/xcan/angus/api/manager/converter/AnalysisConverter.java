package cloud.xcan.angus.api.manager.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.gm.analysis.dto.CustomizationSummaryDto;
import cloud.xcan.angus.api.gm.analysis.vo.SummaryQueryDefinitionVo;
import cloud.xcan.angus.api.gm.analysis.vo.SummaryQueryVo;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryBuilder;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AnalysisConverter {

  public static SummaryQueryBuilder toQueryBuilder(CustomizationSummaryDto dto) {
    if (nonNull(dto.getProjectId())) {
      dto.getFilters().add(SearchCriteria.equal("project_id", dto.getProjectId()));
    }
    return SummaryQueryBuilder.newBuilder().name(dto.getName())
        .groupBy(dto.getGroupBy()).groupByColumns(dto.getGroupByColumns())
        .dateRangeType(dto.getDateRangeType())
        .aggregates(dto.getAggregates())
        .filters(isNotEmpty(dto.getFilters()) ? new HashSet<>(dto.getFilters()) : null)
        .closeMultiTenantCtrl(dto.isCloseMultiTenantCtrl())
        .build();
  }

  public static SummaryQueryDefinitionVo toQueryDefinition(
      Map<String, SummaryQueryRegister> register) {
    SummaryQueryDefinitionVo vo = new SummaryQueryDefinitionVo();
    List<SummaryQueryVo> summaryQuerys = new ArrayList<>();
    for (SummaryQueryRegister value : register.values()) {
      summaryQuerys.add(new SummaryQueryVo().setName(value.name())
          .setTable(value.table())
          .setGroupByColumns(value.groupByColumns())
          .setAggregateColumns(value.aggregateColumns())
          .setHasSysAdmin(value.hasSysAdmin())
          .setHasAnyAuthority(value.hasAnyAuthority())
          .setHasAuthority(value.hasAuthority())
          .setTopAuthority(value.topAuthority())
      );
    }
    vo.setSummaryQuery(summaryQuerys);
    return vo;
  }
}
