package cloud.xcan.angus.core.gm.application.cmd.message.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCurrentCmd;
import cloud.xcan.angus.core.gm.domain.message.MessageCurrentRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of message current command operations for managing user message records.
 * 
 * <p>This class provides comprehensive functionality for message current management including:</p>
 * <ul>
 *   <li>Managing user message records</li>
 *   <li>Handling message read status</li>
 *   <li>Deleting user message records</li>
 *   <li>Updating message statistics</li>
 * </ul>
 * 
 * <p>The implementation ensures proper user message record management
 * with read tracking and statistics updates.</p>
 */
@Slf4j
@Service
public class MessageCurrentCmdImpl extends CommCmd<MessageSent, Long> implements MessageCurrentCmd {

  @Resource
  private MessageCurrentRepo messageCurrentRepo;
  @Resource
  private MessageCmd messageCmd;

  /**
   * Deletes user message records by marking them as deleted.
   * 
   * <p>This method performs soft deletion including:</p>
   * <ul>
   *   <li>Marking message records as deleted</li>
   *   <li>Setting deletion timestamp</li>
   * </ul>
   * 
   * @param ids Set of message record identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Mark message records as deleted with timestamp
        messageCurrentRepo.updateDeletedByIdIn(ids, LocalDateTime.now());
        return null;
      }
    }.execute();
  }

  /**
   * Marks user message records as read.
   * 
   * <p>This method performs read status update including:</p>
   * <ul>
   *   <li>Updating message read status</li>
   *   <li>Setting read timestamp</li>
   *   <li>Incrementing message read count</li>
   * </ul>
   * 
   * @param ids Set of message record identifiers to mark as read
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void read(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Update message read status and timestamp
        messageCurrentRepo.updateReadByIdIn(ids, LocalDateTime.now());
        // Increment message read count
        messageCmd.plusReadNum(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<MessageSent, Long> getRepository() {
    return messageCurrentRepo;
  }
}
