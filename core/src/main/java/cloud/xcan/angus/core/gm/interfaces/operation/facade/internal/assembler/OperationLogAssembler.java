package cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler;


import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogSearchDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.vo.OperationLogVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;


public class OperationLogAssembler {

  public static List<OperationLog> toDomain(List<UserOperation> userOperations) {
    List<OperationLog> operationLogs = new ArrayList<>();
    for (UserOperation optLog : userOperations) {
      OperationLog operationLog = new OperationLog()
          .setRequestId(optLog.getRequestId())
          .setClientId(optLog.getClientId())
          .setResource(OperationResourceType.OTHER)
          .setResourceName(nullSafe(optLog.getResourceName(),
              OperationResourceType.OTHER.getMessage()))
          .setResourceId(optLog.getResourceId())
          .setType(OperationType.CUSTOM)
          .setUserId(optLog.getUserId())
          .setFullName(optLog.getFullName())
          .setOptDate(LocalDateTime.now())
          .setDescription(optLog.getDescription())
          .setDetail(optLog.getDescription())
          .setPrivate0(true);
      operationLog.setTenantId(optLog.getTenantId());
      operationLog.setTenantName(optLog.getTenantName());
      operationLogs.add(operationLog);
    }
    return operationLogs;
  }

  public static OperationLogVo toVo(OperationLog operation) {
    return new OperationLogVo().setId(operation.getId())
        .setRequestId(operation.getRequestId())
        .setClientId(operation.getClientId())
        .setResource(operation.getResource())
        .setResourceName(operation.getResourceName())
        .setResourceId(operation.getResourceId())
        .setType(operation.getType())
        .setUserId(operation.getUserId())
        .setFullName(operation.getFullName())
        .setOptDate(operation.getOptDate())
        .setDescription(operation.getDescription())
        .setDetail(operation.getDetail())
        .setPrivate0(operation.getPrivate0())
        .setTenantId(operation.getTenantId())
        .setTenantName(operation.getTenantName());
  }

  public static Specification<OperationLog> getSpecification(OperationLogFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "optDate")
        .orderByFields("id", "resource", "type", "optDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(OperationLogSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "optDate")
        .orderByFields("id", "resource", "type", "optDate")
        .matchSearchFields("detail")
        .build();
  }
}
