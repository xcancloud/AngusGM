package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.EVENT_CHANNEL;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannelRepo;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of event channel command operations for managing event channels.
 *
 * <p>This class provides comprehensive functionality for event channel management including:</p>
 * <ul>
 *   <li>Creating and configuring event channels</li>
 *   <li>Managing channel configurations and settings</li>
 *   <li>Testing channel connectivity and functionality</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 *
 * <p>The implementation ensures proper event channel management with validation
 * and audit trail maintenance.</p>
 */
@Slf4j
@Service
public class EventChannelCmdImpl extends CommCmd<EventChannel, Long> implements EventChannelCmd {

  @Resource
  private EventChannelRepo eventChannelRepo;
  @Resource
  private EventChannelQuery eventChannelQuery;
  @Resource
  private HashMap<ReceiveChannelType, EventChannelPushCmd> eventChannelPushMap;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates a new event channel with comprehensive validation.
   *
   * <p>This method performs channel creation including:</p>
   * <ul>
   *   <li>Validating channel name uniqueness</li>
   *   <li>Checking channel quota limits</li>
   *   <li>Creating channel configuration</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param channel Event channel configuration to create
   * @return Created channel identifier with name information
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(EventChannel channel) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Validate channel name uniqueness
        eventChannelQuery.checkNameExisted(channel);
        // Validate channel quota
        eventChannelQuery.checkQuota(channel.getType(), 1);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Create channel and record audit log
        IdKey<Long, Object> idKey = insert(channel, "name");
        operationLogCmd.add(EVENT_CHANNEL, channel, CREATED);
        return idKey;
      }
    }.execute();
  }

  /**
   * Replaces an event channel configuration or creates a new one.
   *
   * <p>This method performs channel replacement including:</p>
   * <ul>
   *   <li>Validating channel existence if ID is provided</li>
   *   <li>Checking name uniqueness</li>
   *   <li>Creating new channel or updating existing one</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param channel Event channel configuration to replace
   * @return Channel identifier with name information
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(EventChannel channel) {
    return new BizTemplate<IdKey<Long, Object>>() {
      EventChannel channelDb;

      @Override
      protected void checkParams() {
        // Validate channel exists if ID is provided
        if (nonNull(channel.getId())) {
          channelDb = eventChannelQuery.checkAndFind(channel.getId());
          // Validate channel name uniqueness
          eventChannelQuery.checkNameExisted(channel);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Create new channel if no existing channel found
        if (isNull(channel.getId())) {
          return add(channel);
        }

        // Update existing channel configuration
        eventChannelRepo.save(copyPropertiesIgnoreTenantAuditing(channel, channelDb, "type"));

        // Record operation audit log
        operationLogCmd.add(EVENT_CHANNEL, channelDb, UPDATED);
        return IdKey.of(channel.getId(), channel.getName());
      }
    }.execute();
  }

  /**
   * Deletes an event channel by its identifier.
   *
   * <p>This method performs channel deletion including:</p>
   * <ul>
   *   <li>Validating channel is not in use</li>
   *   <li>Checking channel existence</li>
   *   <li>Deleting channel configuration</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   *
   * @param id Channel identifier to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      EventChannel channelDb;

      @Override
      protected void checkParams() {
        // Validate channel is not in use
        eventChannelQuery.checkNotInUse(id);
        // Validate channel exists
        channelDb = eventChannelQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        // Delete channel configuration
        eventChannelRepo.deleteById(id);
        // Record operation audit log
        operationLogCmd.add(EVENT_CHANNEL, channelDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * Tests event channel connectivity and functionality.
   *
   * <p>This method performs channel testing including:</p>
   * <ul>
   *   <li>Retrieving appropriate push service for channel type</li>
   *   <li>Sending test push notification</li>
   *   <li>Validating push response success</li>
   * </ul>
   *
   * @param eventPush Event push data for testing
   */
  @Override
  public void channelTest(EventPush eventPush) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Send test push and validate response
        ChannelSendResponse response =
            eventChannelPushMap.get(eventPush.getChannelType()).push(eventPush);
        assertTrue(response.isSuccess(), response.getMessage());
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EventChannel, Long> getRepository() {
    return this.eventChannelRepo;
  }
}
