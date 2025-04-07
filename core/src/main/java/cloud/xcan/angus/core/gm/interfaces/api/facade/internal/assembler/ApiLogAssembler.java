package cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.domain.api.log.ApiLog;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLogInfo;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogInfoVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.http.HttpStatusSeries;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ApiLogAssembler {

  public static List<ApiLog> toDomain(
      List<cloud.xcan.angus.core.event.source.ApiLog> apiLogParams) {
    List<ApiLog> logs = new ArrayList<>();
    for (cloud.xcan.angus.core.event.source.ApiLog apiLog : apiLogParams) {
      ApiLog log = new ApiLog()
          .setClientId(apiLog.getClientId())
          .setClientSource(apiLog.getClientSource())
          .setRemote(apiLog.getRemote())
          .setUserId(apiLog.getUserId())
          .setFullName(apiLog.getFullName())
          .setRequestId(apiLog.getRequestId())
          .setServiceName(apiLog.getServiceName())
          .setServiceCode(/* Fix:: Default lower case, inconsistent with service table*/
              isNotEmpty(apiLog.getServiceCode()) ? apiLog.getServiceCode().toUpperCase() : null)
          .setInstanceId(apiLog.getInstanceId())
          .setApiType(apiLog.getApiType())
          //.setApiCode(apiLog.getApiCode())
          //.setApiName(apiLog.getApiName())
          //.setResourceName(apiLog.getResourceName())
          .setUri(apiLog.getUri())
          .setMethod(apiLog.getMethod())
          .setStatus(apiLog.getStatus())
          .setQueryParam(apiLog.getQueryParam())
          .setRequestHeaders(apiLog.getRequestHeaders())
          .setRequestBody(apiLog.getRequestBody())
          .setRequestSize(apiLog.getRequestSize())
          .setRequestDate(apiLog.getRequestDate())
          .setResponseHeaders(apiLog.getResponseHeaders())
          .setResponseBody(apiLog.getResponseBody())
          .setResponseSize(apiLog.getResponseSize())
          .setResponseDate(apiLog.getResponseDate())
          .setSuccess(HttpStatusSeries.resolve(nullSafe(apiLog.getStatus(), 0)).isSuccess())
          .setElapsedMillis(apiLog.getElapsedMillis())
          .setCreatedDate(LocalDateTime.now());
      //.setCreatedDate(apiLog.getCreatedDate());
      log.setTenantId(apiLog.getTenantId());
      log.setTenantName(apiLog.getTenantName());
      logs.add(log);
    }
    return logs;
  }

  public static ApiLogInfoVo toVo(ApiLogInfo apiLogs) {
    return new ApiLogInfoVo()
        .setId(apiLogs.getId())
        .setClientId(apiLogs.getClientId())
        .setClientSource(apiLogs.getClientSource())
        .setRemote(apiLogs.getRemote())
        .setTenantId(apiLogs.getTenantId())
        .setTenantName(apiLogs.getTenantName())
        .setUserId(apiLogs.getUserId())
        .setFullName(apiLogs.getFullName())
        .setRequestId(apiLogs.getRequestId())
        .setServiceName(apiLogs.getServiceName())
        .setServiceCode(apiLogs.getServiceCode())
        .setInstanceId(apiLogs.getInstanceId())
        .setApiType(apiLogs.getApiType())
        .setApiCode(apiLogs.getApiCode())
        .setApiName(apiLogs.getApiName())
        .setResourceName(apiLogs.getResourceName())
        .setUri(apiLogs.getUri())
        .setMethod(apiLogs.getMethod())
        .setStatus(apiLogs.getStatus())
        .setRequestDate(apiLogs.getRequestDate())
        .setSuccess(apiLogs.getSuccess())
        .setElapsedMillis(apiLogs.getElapsedMillis())
        .setCreatedDate(apiLogs.getCreatedDate());
  }

  public static ApiLogDetailVo toDetailVo(ApiLog apiLogs) {
    return new ApiLogDetailVo()
        .setId(apiLogs.getId())
        .setClientId(apiLogs.getClientId())
        .setClientSource(apiLogs.getClientSource())
        .setRemote(apiLogs.getRemote())
        .setTenantId(apiLogs.getTenantId())
        .setTenantName(apiLogs.getTenantName())
        .setUserId(apiLogs.getUserId())
        .setFullName(apiLogs.getFullName())
        .setRequestId(apiLogs.getRequestId())
        .setServiceName(apiLogs.getServiceName())
        .setServiceCode(apiLogs.getServiceCode())
        .setInstanceId(apiLogs.getInstanceId())
        .setApiType(apiLogs.getApiType())
        .setApiCode(apiLogs.getApiCode())
        .setApiName(apiLogs.getApiName())
        .setResourceName(apiLogs.getResourceName())
        .setUri(apiLogs.getUri())
        .setMethod(apiLogs.getMethod())
        .setStatus(apiLogs.getStatus())
        .setQueryParam(apiLogs.getQueryParam())
        .setRequestHeaders(apiLogs.getRequestHeaders())
        .setRequestBody(apiLogs.getRequestBody())
        .setRequestSize(apiLogs.getRequestSize())
        .setRequestDate(apiLogs.getRequestDate())
        .setResponseHeaders(apiLogs.getResponseHeaders())
        .setResponseBody(apiLogs.getResponseBody())
        .setResponseSize(apiLogs.getResponseSize())
        .setResponseDate(apiLogs.getResponseDate())
        .setSuccess(apiLogs.getSuccess())
        .setElapsedMillis(apiLogs.getElapsedMillis())
        .setCreatedDate(apiLogs.getCreatedDate());
  }

  public static GenericSpecification<ApiLogInfo> getSpecification(ApiLogFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "userId", "requestDate")
        .orderByFields("id", "requestDate")
        .build();
    return new GenericSpecification<>(filters);
  }
}
