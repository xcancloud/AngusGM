package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.core.jpa.repository.SimpleSummaryRepository.hasSummaryResource;
import static cloud.xcan.angus.core.utils.CoreUtils.getDateStrBetween;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.spec.utils.ObjectUtils.convert;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.isNull;

import cloud.xcan.angus.api.manager.SimpleSummaryManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.CriteriaUtils;
import cloud.xcan.angus.core.jpa.repository.SummaryRepository;
import cloud.xcan.angus.core.jpa.repository.summary.Aggregate;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryMode;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryBuilder;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Biz
public class SimpleSummaryManagerImpl implements SimpleSummaryManager {

  @Autowired(required = false)
  public SummaryRepository summaryRepository;

  @Override
  public Object getSummary(SummaryQueryBuilder builder) {
    return new BizTemplate<>() {
      @Override
      protected void checkParams() {
        // noop
      }

      @Override
      protected Object process() {
        if (!hasSummaryResource(builder.getName())) {
          return null;
        }

        setMultiTenantCtrl(!builder.isCloseMultiTenantCtrl());
        List<Object[]> summary = summaryRepository.getSummer(builder);
        SummaryMode mode = builder.getSummaryMode();
        return switch (mode) {
          case NO_GROUP -> noGroupSummary(summary);
          case GROUP_BY_STATUS -> groupByStatusSummary(summary);
          case GROUP_BY_DATE -> groupByDateSummary(summary);
        };
      }

      /**
       * <pre>
       *  * ------------ Results ----------
       *  * -- times|COUNT(id)
       *  * -- ----------------------
       *  * -- 2022-13	11
       *  * -- 2022-14	3
       *  * -- 2022-15	2
       *  * -- 2022-18	1
       *  * -- 2022-25	1
       *  * -- 2022-26	1
       *  * -- 2022-33	1
       *  * -- 2022-35	1
       *  * ------------ Model ------------
       *  * Map<String,Map<String,Long>> :: times -> COUNT_id -> summary
       * </pre>
       */
      private Map<String, Map<String, BigDecimal>> groupByDateSummary(List<Object[]> summary) {
        Map<String, Map<String, BigDecimal>> groupByDateResults = new HashMap<>();
        if (isEmpty(summary)) {
          return groupByDateResults;
        }

        String columnName = "";
        for (Object[] row : summary) {
          Map<String, BigDecimal> values = new LinkedHashMap<>();
          for (int i = 0; i < builder.getAggregates().size(); i++) {
            Aggregate aggregate = builder.getAggregates().get(i);
            values.put(/*COUNT_id*/aggregate.toColumnName(),/*summary*/convertValue(row[i + 1]));
            if (isEmpty(columnName)) {
              columnName = aggregate.toColumnName();
            }
          }
          groupByDateResults.put(/*times*/convert(row[0], String.class), values);
        }

        Map<String, Map<String, BigDecimal>> defaultAndSortedMap = new LinkedHashMap<>();
        String groupDateColumn = builder.getGroupByColumns().get(0);
        String startDate = CriteriaUtils.findFirstValue(builder.getFilters(), groupDateColumn,
            SearchOperation.GREATER_THAN_EQUAL);
        String endDate = CriteriaUtils.findFirstValue(builder.getFilters(), groupDateColumn,
            SearchOperation.LESS_THAN_EQUAL);
        Map<String, BigDecimal> defaultValue = Map
            .of(columnName, BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP));
        List<String> dateRanges = getDateStrBetween(startDate, endDate, builder.getDateRangeType());
        for (String dateRange : dateRanges) {
          defaultAndSortedMap
              .put(dateRange, groupByDateResults.getOrDefault(dateRange, defaultValue));
        }
        return defaultAndSortedMap;
      }

      /**
       * <pre>
       *  * ------------ Results ----------
       *  * -- source|gender|admin|enabled|locked|COUNT(id)
       *  * -- ----------------------
       *  * -- BACKGROUND_ADDED	MALE	0	0	1	1
       *  * -- BACKGROUND_ADDED	MALE	0	1	0	14
       *  * -- BACKGROUND_ADDED	MALE	1	1	0	4
       *  * -- INVITATION_CODE_SIGNUP	UNKNOWN	0	1	0	1
       *  * -- INVITATION_CODE_SIGNUP	UNKNOWN	1	1	0	1
       *  * ------------ Model ------------
       *  * Map<String,Map<String,Map<String,Long>>> :: source -> status... -> COUNT_id + TOTAL -> status... + TOTAL summary
       *  </pre>
       */
      private Map<String, Map<String, Map<String, BigDecimal>>> groupByStatusSummary(
          List<Object[]> summary) {
        int groupColumnNum = builder.getGroupByColumns().size();
        int aggregateColumnNum = builder.getAggregates().size();
        Map<String, Map<String, Map<String, BigDecimal>>> groupByStatusResults = new HashMap<>();
        for (int gb = 0; gb < groupColumnNum; gb++) {
          Map<String, Map<String, BigDecimal>> statusAggregateValues = new HashMap<>(); // GET or Put
          for (int af = 0; af < aggregateColumnNum; af++) {
            int groupColumnIdx = gb, aggregateColumnIdx = groupColumnNum + af;
            Map<String, BigDecimal> statusValuesMap = summary.stream().collect(Collectors.toMap(
                x -> convert(x[groupColumnIdx], String.class),
                x -> convertValue(x[aggregateColumnIdx]), BigDecimal::add));

            for (String status : statusValuesMap.keySet()) {
              if (isNull(status)) { // Fix:: Grouping field value may be null
                continue;
              }
              //aggregateValues.put(status, statusValuesMap.get(status));
              if (!statusAggregateValues.containsKey(status)) {
                LinkedHashMap<String, BigDecimal> aggregateValues = new LinkedHashMap<>();
                aggregateValues.put(builder.getAggregates().get(af).toColumnName(),
                    statusValuesMap.get(status));
                statusAggregateValues.put(status, aggregateValues);
              } else {
                statusAggregateValues.get(status)
                    .put(builder.getAggregates().get(af).toColumnName(),
                        statusValuesMap.get(status));
              }
              statusAggregateValues.get(status)
                  .put("TOTAL_" + builder.getAggregates().get(af).toColumnName(),
                      BigDecimal.valueOf(
                          statusValuesMap.values().stream().mapToDouble(BigDecimal::doubleValue)
                              .sum()));
            }
          }
          String resourceName = builder.getGroupByColumns().get(gb);
          if (groupByStatusResults.containsKey(resourceName)) {
            groupByStatusResults.get(resourceName).putAll(statusAggregateValues);
          } else {
            groupByStatusResults.put(resourceName, statusAggregateValues);
          }
        }
        return groupByStatusResults;
      }

      /**
       * <pre>
       *  * ------------ Results ----------
       *  * -- COUNT(id)
       *  * -- ---
       *  * -- 21
       *  * ------------ Model ------------
       *  * Map<String,Long> :: COUNT_id -> summary
       *  </pre>
       */
      private Map<String, BigDecimal> noGroupSummary(List<Object[]> summary) {
        Map<String, BigDecimal> noGroupResults = new LinkedHashMap<>();
        for (int i = 0; i < builder.getAggregates().size(); i++) {
          Aggregate aggregate = builder.getAggregates().get(i);
          if (builder.getAggregates().size() > 1) {
            if (Objects.nonNull(summary.get(0)[i])) {
              noGroupResults.put(/*COUNT_id*/aggregate.toColumnName(),
                  /*summary*/convertValue(((Object) summary.get(0)[i]).toString()));
            } else {
              noGroupResults.put(/*COUNT_id*/aggregate.toColumnName(),/*summary*/
                  BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
          } else {
            noGroupResults.put(/*COUNT_id*/aggregate.toColumnName(),
                /*summary*/convertValue(((Object) summary.get(0)).toString()));
          }
        }
        return noGroupResults;
      }
    }.execute();
  }

  @Override
  public Map<String, Object> getSummary(List<SummaryQueryBuilder> builders) {
    return new BizTemplate<Map<String, Object>>() {
      @Override
      protected Map<String, Object> process() {
        Map<String, Object> summaries = new HashMap<>();
        for (SummaryQueryBuilder builder : builders) {
          summaries.put(builder.getName(), getSummary(builder));
        }
        return summaries;
      }
    }.execute();
  }

  public static BigDecimal convertValue(Object value) {
    try {
      String strValue = value.toString();
      return strValue.contains(".") ? BigDecimal.valueOf(Double.parseDouble(strValue))
          .setScale(2, RoundingMode.HALF_UP)
          : BigDecimal.valueOf(Long.parseLong(value.toString())).setScale(0, RoundingMode.HALF_UP);
    } catch (Exception var3) {
      var3.printStackTrace();
    }
    return BigDecimal.ZERO;
  }
}
