package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFailureFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogSensitiveFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogUserHistoryDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.SensitiveLogVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Audit log assembler
 * </p>
 */
public class AuditLogAssembler {

  /**
   * <p>
   * Convert Domain to VO
   * </p>
   */
  public static AuditLogVo toVo(AuditLog log) {
    AuditLogVo vo = new AuditLogVo();
    vo.setId(String.valueOf(log.getId()));
    vo.setUserId(log.getUserId() != null ? String.valueOf(log.getUserId()) : null);
    vo.setUserName(log.getUsername());
    vo.setModule(log.getModule());
    vo.setOperation(log.getOperation());
    vo.setDescription(log.getDescription());
    vo.setLevel(log.getLevel());
    vo.setIpAddress(log.getIpAddress());
    vo.setLocation(log.getLocation());
    vo.setDevice(log.getDevice());
    vo.setOperationTime(log.getOperationTime());
    vo.setDuration(log.getDuration());
    vo.setSuccess(log.getSuccess());
    vo.setRequestData(log.getRequestData() != null ? (Map<String, Object>) log.getRequestData() : null);
    vo.setResponseData(log.getResponseData() != null ? (Map<String, Object>) log.getResponseData() : null);
    
    // Set auditing fields
    vo.setTenantId(log.getTenantId());
    vo.setCreatedBy(log.getCreatedBy());
    vo.setCreatedDate(log.getCreatedDate());
    vo.setModifiedBy(log.getModifiedBy());
    vo.setModifiedDate(log.getModifiedDate());
    
    return vo;
  }

  /**
   * <p>
   * Convert Domain to DetailVO
   * </p>
   */
  public static AuditLogDetailVo toDetailVo(AuditLog log) {
    AuditLogDetailVo vo = new AuditLogDetailVo();
    vo.setId(String.valueOf(log.getId()));
    vo.setUserId(log.getUserId() != null ? String.valueOf(log.getUserId()) : null);
    vo.setUserName(log.getUsername());
    vo.setModule(log.getModule());
    vo.setOperation(log.getOperation());
    vo.setDescription(log.getDescription());
    vo.setLevel(log.getLevel());
    vo.setIpAddress(log.getIpAddress());
    vo.setLocation(log.getLocation());
    vo.setDevice(log.getDevice());
    vo.setUserAgent(log.getUserAgent());
    vo.setOperationTime(log.getOperationTime());
    vo.setDuration(log.getDuration());
    vo.setSuccess(log.getSuccess());
    vo.setRequestUrl(log.getRequestUrl());
    vo.setRequestMethod(log.getRequestMethod());
    vo.setRequestHeaders(log.getRequestHeaders() != null ? (Map<String, String>) log.getRequestHeaders() : null);
    vo.setRequestData(log.getRequestData() != null ? (Map<String, Object>) log.getRequestData() : null);
    vo.setResponseStatus(log.getResponseStatus());
    vo.setResponseHeaders(log.getResponseHeaders() != null ? (Map<String, String>) log.getResponseHeaders() : null);
    vo.setResponseData(log.getResponseData() != null ? (Map<String, Object>) log.getResponseData() : null);
    
    // Convert changes
    if (log.getChanges() != null) {
      List<AuditLogDetailVo.Change> changes = new ArrayList<>();
      Map<String, Object> changesMap = (Map<String, Object>) log.getChanges();
      for (Map.Entry<String, Object> entry : changesMap.entrySet()) {
        AuditLogDetailVo.Change change = new AuditLogDetailVo.Change();
        change.setField(entry.getKey());
        if (entry.getValue() instanceof Map) {
          Map<String, Object> changeValue = (Map<String, Object>) entry.getValue();
          change.setOldValue(changeValue.get("oldValue"));
          change.setNewValue(changeValue.get("newValue"));
        }
        changes.add(change);
      }
      vo.setChanges(changes);
    }
    
    // Set auditing fields
    vo.setTenantId(log.getTenantId());
    vo.setCreatedBy(log.getCreatedBy());
    vo.setCreatedDate(log.getCreatedDate());
    vo.setModifiedBy(log.getModifiedBy());
    vo.setModifiedDate(log.getModifiedDate());
    
    return vo;
  }

  /**
   * <p>
   * Convert Domain to SensitiveLogVO
   * </p>
   */
  public static SensitiveLogVo toSensitiveLogVo(AuditLog log) {
    SensitiveLogVo vo = new SensitiveLogVo();
    vo.setId(String.valueOf(log.getId()));
    vo.setUserId(log.getUserId() != null ? String.valueOf(log.getUserId()) : null);
    vo.setUserName(log.getUsername());
    vo.setModule(log.getModule());
    vo.setOperation(log.getOperation());
    vo.setDescription(log.getDescription());
    vo.setLevel(log.getLevel());
    vo.setOperationTime(log.getOperationTime());
    vo.setIpAddress(log.getIpAddress());
    vo.setSuccess(log.getSuccess());
    return vo;
  }

  /**
   * <p>
   * Build query specification from FindDto
   * </p>
   */
  public static GenericSpecification<AuditLog> getSpecification(AuditLogFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "operationTime")
        .orderByFields("id", "createdDate", "modifiedDate", "operationTime")
        .matchSearchFields("description")
        .build();
    return new GenericSpecification<>(filters);
  }

  /**
   * <p>
   * Build query specification for user history
   * </p>
   */
  public static GenericSpecification<AuditLog> getUserHistorySpecification(Long userId, AuditLogUserHistoryDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "operationTime")
        .orderByFields("id", "createdDate", "modifiedDate", "operationTime")
        .build();
    // Add userId filter manually
    filters.add(new SearchCriteria("userId", userId, SearchOperation.EQUAL));
    return new GenericSpecification<>(filters);
  }

  /**
   * <p>
   * Build query specification for sensitive logs
   * </p>
   */
  public static GenericSpecification<AuditLog> getSensitiveLogsSpecification(AuditLogSensitiveFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "operationTime")
        .orderByFields("id", "createdDate", "modifiedDate", "operationTime")
        .build();
    // Add level filter manually
    filters.add(new SearchCriteria("level", "warning", SearchOperation.EQUAL));
    return new GenericSpecification<>(filters);
  }

  /**
   * <p>
   * Build query specification for failure logs
   * </p>
   */
  public static GenericSpecification<AuditLog> getFailureLogsSpecification(AuditLogFailureFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "operationTime")
        .orderByFields("id", "createdDate", "modifiedDate", "operationTime")
        .build();
    // Add success filter manually
    filters.add(new SearchCriteria("success", false, SearchOperation.EQUAL));
    return new GenericSpecification<>(filters);
  }
}
