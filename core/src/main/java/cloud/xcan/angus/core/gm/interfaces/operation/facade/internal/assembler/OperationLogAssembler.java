package cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler;


import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationFindDto;
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
          .setClientId(optLog.getClientId())
          .setRequestId(optLog.getRequestId())
          .setResourceName(optLog.getResourceName())
          .setUserId(optLog.getUserId())
          .setFullName(optLog.getFullName())
          .setDescription(optLog.getDescription())
          .setSuccess(optLog.getSuccess())
          .setOptDate(LocalDateTime.now())
          .setFailureReason(optLog.getFailureReason());
      operationLog.setTenantId(optLog.getTenantId());
      operationLog.setTenantName(optLog.getTenantName());
      operationLogs.add(operationLog);
    }
    return operationLogs;
  }

  public static OperationLogVo toVo(OperationLog optLog) {
    return new OperationLogVo().setId(optLog.getId())
        .setClientId(optLog.getClientId())
        .setRequestId(optLog.getRequestId())
        .setResourceName(optLog.getResourceName())
        .setUserId(optLog.getUserId())
        .setFullName(optLog.getFullName())
        .setDescription(optLog.getDescription())
        .setSuccess(optLog.getSuccess())
        .setFailureReason(optLog.getFailureReason())
        .setOptDate(optLog.getOptDate())
        .setTenantId(optLog.getTenantId())
        .setTenantName(optLog.getTenantName());
  }

  public static Specification<OperationLog> getSpecification(OperationFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "optDate")
        .orderByFields("id", "optDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
