package cloud.xcan.angus.core.gm.application.query.event.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.event.EventQuery;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.EventRepo;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


@Slf4j
@Biz
@SummaryQueryRegister(name = "Event", table = "event",
    groupByColumns = {"created_date", "type", "push_status"})
public class EventQueryImpl implements EventQuery {

  @Resource
  private EventRepo eventRepo;

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

  @Override
  public Page<Event> list(Specification<Event> spec, PageRequest pageable) {
    return new BizTemplate<Page<Event>>() {

      @Override
      protected Page<Event> process() {
        return eventRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Event checkAndFind(Long id) {
    return eventRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Event"));
  }

  @Override
  public List<Event> findEventInUnPush(int size) {
    return eventRepo.findAllByPushStatus(EventPushStatus.PENDING.getValue(), size);
  }

}
