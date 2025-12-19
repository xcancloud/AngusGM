package cloud.xcan.angus.core.gm.application.query.event.impl;


import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.event.EventQuery;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.EventRepo;
import cloud.xcan.angus.core.gm.domain.event.EventSearchRepo;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of event query operations.
 * </p>
 * <p>
 * Manages event retrieval, validation, and search capabilities. Provides comprehensive event
 * querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports event detail retrieval, paginated listing, full-text search, and pending event queries
 * for comprehensive event management.
 * </p>
 */
@Slf4j
@org.springframework.stereotype.Service
@SummaryQueryRegister(name = "Event", table = "event",
    groupByColumns = {"created_date", "type", "push_status"})
public class EventQueryImpl implements EventQuery {

  @Resource
  private EventRepo eventRepo;
  @Resource
  private EventSearchRepo eventSearchRepo;

  /**
   * <p>
   * Retrieves detailed event information by ID.
   * </p>
   * <p>
   * Fetches complete event record with validation. Throws ResourceNotFound exception if event does
   * not exist.
   * </p>
   */
  @Override
  public Event detail(Long id) {
    return new BizTemplate<Event>() {
      Event event;

      @Override
      protected void checkParams() {
        event = checkAndFind(id);
      }

      @Override
      protected Event process() {
        return event;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves events with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering. Returns paginated results for
   * comprehensive event management.
   * </p>
   */
  @Override
  public Page<Event> list(GenericSpecification<Event> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Event>>() {

      @Override
      protected Page<Event> process() {
        return fullTextSearch
            ? eventSearchRepo.find(spec.getCriteria(), pageable, Event.class, match)
            : eventRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves event by ID.
   * </p>
   * <p>
   * Verifies event exists and returns event information. Throws ResourceNotFound exception if event
   * does not exist.
   * </p>
   */
  @Override
  public Event checkAndFind(Long id) {
    return eventRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Event"));
  }

  /**
   * <p>
   * Retrieves unprocessed events for push processing.
   * </p>
   * <p>
   * Returns events with pending push status for processing. Limits results by size for processing
   * control.
   * </p>
   */
  @Override
  public List<Event> findEventInUnPush(int size) {
    return eventRepo.findAllByPushStatus(EventPushStatus.PENDING.getValue(), size);
  }

}
