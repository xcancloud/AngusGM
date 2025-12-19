package cloud.xcan.angus.core.gm.application.cmd.operation.impl;

import static cloud.xcan.angus.core.gm.application.converter.OperationLogConverter.toOperation;
import static cloud.xcan.angus.core.gm.application.converter.OperationLogConverter.toOperations;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.register.SettingPropertiesRegister;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogRepo;
import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.experimental.Resources;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of operation log command operations for managing system audit trails.
 *
 * <p>This class provides comprehensive functionality for operation logging including:</p>
 * <ul>
 *   <li>Recording user and system operations</li>
 *   <li>Managing operation log entries</li>
 *   <li>Cleaning up old operation logs</li>
 *   <li>Supporting audit trail requirements</li>
 * </ul>
 *
 * <p>The implementation ensures proper audit trail maintenance with
 * configurable logging and cleanup capabilities.</p>
 */
@Slf4j
@org.springframework.stereotype.Service
public class OperationLogCmdImpl extends CommCmd<OperationLog, Long> implements OperationLogCmd {

  @Resource
  private OperationLogRepo operationLogRepo;
  @Resource
  private SettingPropertiesRegister settingPropertiesRegister;

  /**
   * Adds a single operation log entry with resource information.
   *
   * <p>This method performs operation logging including:</p>
   * <ul>
   *   <li>Converting resource to operation log</li>
   *   <li>Validating logging conditions</li>
   *   <li>Recording operation details</li>
   * </ul>
   *
   * @param resourceType Type of resource being operated on
   * @param resource     Resource entity being operated on
   * @param operation    Type of operation performed
   * @param params       Additional operation parameters
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void add(OperationResourceType resourceType, Resources<?> resource,
      OperationType operation, Object... params) {
    add(toOperation(resourceType, resource, operation, params));
  }

  /**
   * Adds a single operation log entry.
   *
   * <p>This method validates logging conditions and records the operation
   * only when appropriate conditions are met.</p>
   *
   * @param operation Operation log entry to add
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void add(OperationLog operation) {
    if (nonNull(operation) && (isUserAction() || isNotEmpty(operation.getFullName()))
        && settingPropertiesRegister.enabledOperationLog()) {
      insert0(operation);
    }
  }

  /**
   * Adds multiple operation log entries with resource information.
   *
   * <p>This method performs batch operation logging including:</p>
   * <ul>
   *   <li>Converting resources to operation logs</li>
   *   <li>Validating logging conditions</li>
   *   <li>Recording operation details</li>
   * </ul>
   *
   * @param resourceType Type of resource being operated on
   * @param resources    List of resource entities being operated on
   * @param operation    Type of operation performed
   * @param params       Additional operation parameters
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void addAll(OperationResourceType resourceType,
      List<? extends Resources<?>> resources, OperationType operation, Object... params) {
    addAll(toOperations(resourceType, resources, operation, params));
  }

  /**
   * Adds multiple operation log entries.
   *
   * <p>This method validates logging conditions and records operations
   * only when appropriate conditions are met.</p>
   *
   * @param operations List of operation log entries to add
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void addAll(List<OperationLog> operations) {
    if (isNotEmpty(operations)
        && (isUserAction() || isNotEmpty(operations.get(0).getFullName()))
        && settingPropertiesRegister.enabledOperationLog()) {
      batchInsert0(operations);
    }
  }

  /**
   * Adds multiple operation log entries with return values.
   *
   * <p>This method performs batch operation logging and returns
   * the created log entry identifiers.</p>
   *
   * @param operations List of operation log entries to add
   * @return List of created operation log identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<OperationLog> operations) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        return batchInsert(operations);
      }
    }.execute();
  }

  /**
   * Clears operation logs older than specified days.
   *
   * <p>This method performs log cleanup including:</p>
   * <ul>
   *   <li>Calculating cutoff date</li>
   *   <li>Deleting old operation logs</li>
   *   <li>Maintaining database performance</li>
   * </ul>
   *
   * @param clearBeforeDay Number of days before which logs should be cleared
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void clearOperationLog(Integer clearBeforeDay) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Calculate cutoff date for log cleanup
        LocalDateTime delDate = LocalDateTime.now().minusDays(clearBeforeDay);
        // Delete operation logs older than cutoff date
        operationLogRepo.deleteByOptDateBefore(delDate);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<OperationLog, Long> getRepository() {
    return operationLogRepo;
  }
}
