package cloud.xcan.angus.core.gm.application.query.event.impl;

import cloud.xcan.angus.core.gm.application.query.event.EventPushQuery;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushRepo;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Implementation of event push query operations.
 * </p>
 * <p>
 * Manages event push retrieval and pending event processing.
 * Provides comprehensive event push querying for event processing.
 * </p>
 * <p>
 * Supports pending event retrieval with retry limit validation
 * for comprehensive event push management.
 * </p>
 */
@Slf4j
@Service
public class EventPushQueryImpl implements EventPushQuery {

  @Resource
  private EventPushRepo eventPushRepo;

  /**
   * <p>
   * Retrieves pending event push records for processing.
   * </p>
   * <p>
   * Returns events that are pending for push with retry limit validation.
   * Limits results by size and maximum retry number for processing control.
   * </p>
   */
  @Override
  public List<EventPush> findPushEventInPending(int size, int maxRetryNum) {
    return eventPushRepo.findPushEventInPending(size, maxRetryNum);
  }

}
