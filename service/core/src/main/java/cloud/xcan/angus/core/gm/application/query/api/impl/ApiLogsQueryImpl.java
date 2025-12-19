package cloud.xcan.angus.core.gm.application.query.api.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.api.ApiLogsQuery;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLog;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLogInfo;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLogInfoRepo;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLogRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.http.PathMatchers;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * <p>
 * Implementation of API logs query operations.
 * </p>
 * <p>
 * Manages API log retrieval, filtering, and information enrichment. Provides comprehensive API log
 * querying with summary statistics support.
 * </p>
 * <p>
 * Supports API log detail retrieval, paginated listing, and API information enrichment for log
 * analysis and monitoring.
 * </p>
 */
@Slf4j
@org.springframework.stereotype.Service
@SummaryQueryRegister(name = "ApiLogs", table = "api_log",
    groupByColumns = {"request_date", "api_type", "method", "status", "success", "client_source"})
public class ApiLogsQueryImpl implements ApiLogsQuery {

  @Resource
  private ApiLogRepo apiLogsRepo;
  @Resource
  private ApiLogInfoRepo apiLogsInfoRepo;
  @Resource
  private ApiRepo commonApiRepo;

  /**
   * <p>
   * Retrieves detailed API log information by ID.
   * </p>
   * <p>
   * Fetches complete API log record with all associated information. Throws ResourceNotFound
   * exception if log does not exist.
   * </p>
   */
  @Override
  public ApiLog detail(Long id) {
    return new BizTemplate<ApiLog>(true, true) {

      @Override
      protected ApiLog process() {
        return apiLogsRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "ApiLog"));
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves paginated list of API log information.
   * </p>
   * <p>
   * Supports filtering and pagination for API log analysis. Returns enriched API log information
   * with summary statistics.
   * </p>
   */
  @Override
  public Page<ApiLogInfo> list(GenericSpecification<ApiLogInfo> spec, Pageable pageable) {
    return new BizTemplate<Page<ApiLogInfo>>(true, true) {

      @Override
      protected Page<ApiLogInfo> process() {
        return apiLogsInfoRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Enriches API logs with API information.
   * </p>
   * <p>
   * Matches API logs with corresponding API definitions based on service code, method, and URI
   * pattern matching. Enhances logs with API code, name, and resource information for better
   * analysis.
   * </p>
   */
  @Override
  public void joinApiInfo(List<ApiLog> apiLogs) {
    // Group API logs by service code for efficient processing
    Map<String, List<ApiLog>> serviceApiLogMap = apiLogs.stream()
        .collect(Collectors.groupingBy(ApiLog::getServiceCode));

    for (String serviceCode : serviceApiLogMap.keySet()) {
      // Retrieve APIs for the service and group by HTTP method
      Map<String, List<Api>> serviceApisMap = commonApiRepo
          .findAllByServiceCode(serviceCode.toUpperCase()).stream()
          .collect(Collectors.groupingBy(x -> x.getMethod().getValue()));

      if (isNotEmpty(serviceApisMap)) {
        // Process each API log for the service
        for (ApiLog apiLog : serviceApiLogMap.get(serviceCode)) {
          List<Api> methodApis = serviceApisMap.get(apiLog.getMethod());
          if (isNotEmpty(methodApis)) {
            // Match API log URI with API definition URI patterns
            for (Api methodApi : methodApis) {
              if (PathMatchers.getPathMatcher().match(methodApi.getUri(), apiLog.getUri())) {
                // Enrich API log with API information
                apiLog.setApiCode(methodApi.getOperationId()).setApiName(methodApi.getName())
                    .setResourceName(methodApi.getResourceName());
              }
            }
          }
        }
      }
    }
  }
}
