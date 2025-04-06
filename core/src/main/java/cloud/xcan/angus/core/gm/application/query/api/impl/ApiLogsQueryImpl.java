package cloud.xcan.angus.core.gm.application.query.api.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.core.biz.Biz;
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

@Slf4j
@Biz
@SummaryQueryRegister(name = "ApiLogs", table = "api_log",
    groupByColumns = {"request_date", "api_type", "method", "status", "success", "client_source"})
public class ApiLogsQueryImpl implements ApiLogsQuery {

  @Resource
  private ApiLogRepo apiLogsRepo;

  @Resource
  private ApiLogInfoRepo apiLogsInfoRepo;

  @Resource
  private ApiRepo commonApiRepo;

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

  @Override
  public Page<ApiLogInfo> list(GenericSpecification<ApiLogInfo> spec, Pageable pageable) {
    return new BizTemplate<Page<ApiLogInfo>>(true, true) {

      @Override
      protected Page<ApiLogInfo> process() {
        return apiLogsInfoRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public void joinApiInfo(List<ApiLog> apiLogs) {
    Map<String, List<ApiLog>> serviceApiLogMap = apiLogs.stream()
        .collect(Collectors.groupingBy(ApiLog::getServiceCode));
    for (String serviceCode : serviceApiLogMap.keySet()) {
      Map<String, List<Api>> serviceApisMap = commonApiRepo
          .findAllByServiceCode(serviceCode.toUpperCase()).stream()
          .collect(Collectors.groupingBy(x -> x.getMethod().getValue()));
      if (isNotEmpty(serviceApisMap)) {
        for (ApiLog apiLog : serviceApiLogMap.get(serviceCode)) {
          List<Api> methodApis = serviceApisMap.get(apiLog.getMethod());
          if (isNotEmpty(methodApis)) {
            for (Api methodApi : methodApis) {
              if (PathMatchers.getPathMatcher().match(methodApi.getUri(), apiLog.getUri())) {
                apiLog.setApiCode(methodApi.getCode()).setApiName(methodApi.getName())
                    .setResourceName(methodApi.getResourceName());
              }
            }
          }
        }
      }
    }
  }
}
