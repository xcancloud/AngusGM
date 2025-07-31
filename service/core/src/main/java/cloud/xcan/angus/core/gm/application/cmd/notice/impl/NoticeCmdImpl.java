package cloud.xcan.angus.core.gm.application.cmd.notice.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.NOTICE;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.notice.NoticeCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.notice.NoticeQuery;
import cloud.xcan.angus.core.gm.domain.notice.Notice;
import cloud.xcan.angus.core.gm.domain.notice.NoticeRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of notice command operations for managing system notices.
 * 
 * <p>This class provides comprehensive functionality for notice management including:</p>
 * <ul>
 *   <li>Creating and publishing system notices</li>
 *   <li>Managing notice timing and scheduling</li>
 *   <li>Deleting notice records</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>The implementation ensures proper notice management with validation
 * and audit trail maintenance.</p>
 */
@Slf4j
@Service
public class NoticeCmdImpl extends CommCmd<Notice, Long> implements NoticeCmd {

  @Resource
  private NoticeRepo noticeRepo;
  @Resource
  private NoticeQuery noticeQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates a new notice with comprehensive validation.
   * 
   * <p>This method performs notice creation including:</p>
   * <ul>
   *   <li>Validating notice timing parameters</li>
   *   <li>Creating notice record</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param notice Notice to create
   * @return Created notice identifier
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Notice notice) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Validate notice timing parameters
        noticeQuery.checkAppSendTimingParam(notice);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Create notice record
        IdKey<Long, Object> idKey = insert(notice);
        // Record operation audit log
        operationLogCmd.add(NOTICE, notice, CREATED);
        return idKey;
      }
    }.execute();
  }

  /**
   * Deletes notices by their identifiers.
   * 
   * <p>This method performs notice deletion including:</p>
   * <ul>
   *   <li>Retrieving notice information for audit logs</li>
   *   <li>Deleting notice records</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param ids List of notice identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(List<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Retrieve notices for audit logging
        List<Notice> notices = noticeRepo.findAllById(ids);
        if (isNotEmpty(notices)){
          // Delete notice records
          noticeRepo.deleteByIdIn(ids);
          // Record operation audit logs
          operationLogCmd.addAll(NOTICE, notices, DELETED);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Notice, Long> getRepository() {
    return noticeRepo;
  }
}
