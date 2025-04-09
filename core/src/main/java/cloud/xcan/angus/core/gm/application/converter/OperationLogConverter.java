package cloud.xcan.angus.core.gm.application.converter;


import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.spec.locale.MessageHolder.message;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getDefaultLanguage;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getRequestId;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.operation.OperationResource;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
import cloud.xcan.angus.spec.locale.EnumValueMessage;
import cloud.xcan.angus.spec.principal.Principal;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OperationLogConverter {

  public static <T extends OperationResource<?>> OperationLog toOperation(
      OperationResourceType type, T resource, OperationType operation, Object... params) {
    return assembleOperationLog(type, resource, operation, PrincipalContext.get(), params);
  }

  public static List<OperationLog> toOperations(OperationResourceType type,
      List<? extends OperationResource<?>> resources, OperationType operation,
      List<Object[]> params) {
    Principal principal = PrincipalContext.get();
    Long tenantId = nonNull(principal.getTenantId()) ? principal.getTenantId() : -1L;
    List<OperationLog> operations = new ArrayList<>(resources.size());
    for (int i = 0; i < resources.size(); i++) {
      OperationLog operation0 = assembleOperationLog(type, resources.get(i), operation,
          principal, params.get(i));
      operation0.setTenantId(tenantId);
      operations.add(operation0);
    }
    return operations;
  }

  public static List<OperationLog> toOperations(OperationResourceType type,
      List<? extends OperationResource<?>> resources, OperationType operation, Object... params) {
    Principal principal = PrincipalContext.get();
    Long tenantId = nonNull(principal.getTenantId()) ? principal.getTenantId() : -1L;
    List<OperationLog> operations = new ArrayList<>(resources.size());
    for (OperationResource<?> resource : resources) {
      OperationLog operation0 = assembleOperationLog(type, resource, operation, principal, params);
      operation0.setTenantId(tenantId);
      operations.add(operation0);
    }
    return operations;
  }

  /**
   * Support max three parameters, need to keep order
   *
   * @param params The last parameter is the resource name
   */
  private static OperationLog assembleOperationLog(OperationResourceType resourceType,
      OperationResource<?> resource, OperationType operationType, Principal principal,
      Object[] params) {
    OperationLog operation0 = new OperationLog()
        .setRequestId(getRequestId())
        .setClientId(getClientId())
        .setResource(resourceType)
        .setResourceName(resource.getName())
        .setResourceId(resource.getId().toString())
        .setType(operationType)
        .setUserId(principal.getUserId())
        .setFullName(principal.getFullName())
        .setOptDate(LocalDateTime.now())
        .setDescription(assembleDescription(resourceType, operationType, params))
        .setDetail(assembleDetail(resourceType, resource, operationType, params))
        .setPrivate0(resourceType.isPrivate0());
    operation0.setTenantId(principal.getTenantId());
    operation0.setTenantName(principal.getTenantName());
    return operation0;
  }

  /**
   * The resource name does not need to be displayed in the description operation0.
   */
  private static String assembleDescription(OperationResourceType type,
      OperationType operation, Object[] params) {
    if (ObjectUtils.isEmpty(params)) {
      return message(operation.getDescMessageKey(),
          new Object[]{type.getMessage()}, getDefaultLanguage().toLocale());
    }

    assertTrue(params.length <= 2, "Support max two parameters");
    if (params.length == 1) {
      // Move the resource name to the front
      return message(operation.getDescMessageKey(),
          new Object[]{type.getMessage(), safeEnumString(params[0])
              , getDefaultLanguage().toLocale()});
    }
    // Move the resource name to the front
    return message(operation.getDescMessageKey(),
        new Object[]{type.getMessage(), safeEnumString(params[0]),
            safeEnumString(params[1])}, getDefaultLanguage().toLocale());
  }

  /**
   * The resource name needs to be displayed in the detail operation0.
   * <p>
   * Set the resource name to the second parameter position.
   */
  private static String assembleDetail(OperationResourceType resourceType,
      OperationResource<?> resource, OperationType operation, Object[] params) {
    if (ObjectUtils.isEmpty(params)) {
      return message(operation.getDetailMessageKey(),
          new Object[]{resourceType.getMessage(), "[" + resource.getName() + "]"
              , getDefaultLanguage().toLocale()});
    }
    assertTrue(params.length <= 2, "Support max two parameters");
    if (params.length == 1) {
      // Move the resource name to the front
      return message(operation.getDetailMessageKey(),
          new Object[]{resourceType.getMessage(), "[" + resource.getName() + "]",
              safeEnumString(params[0])}, getDefaultLanguage().toLocale());
    }
    // Move the resource name to the front
    return message(operation.getDetailMessageKey(),
        new Object[]{resourceType.getMessage(), "[" + resource.getName() + "]",
            safeEnumString(params[0]), safeEnumString(params[1])}, getDefaultLanguage().toLocale());
  }

  private static String safeEnumString(Object param) {
    if (param instanceof EnumValueMessage) {
      return ((EnumValueMessage<?>) param).getMessage();
    }
    return param.toString();
  }

  public static List<String[]> activityParams(
      Collection<? extends OperationResource<?>> resources) {
    List<String[]> params = new ArrayList<>(resources.size());
    for (OperationResource<?> resource : resources) {
      params.add(new String[]{"[" + resource.getName() + "]"});
    }
    return params;
  }
}
