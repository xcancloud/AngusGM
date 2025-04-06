package cloud.xcan.angus.core.gm.application.cmd.api.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.api.ApiLogCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiLogsQuery;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLog;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLogRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


@DoInFuture("Sharding by tenant")
@Slf4j
@Biz
public class ApiLogCmdImpl extends CommCmd<ApiLog, Long> implements ApiLogCmd {

  @Resource
  private ApiLogRepo apiLogsRepo;

  @Resource
  private ApiLogsQuery apiLogsQuery;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<ApiLog> apiLogs) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        apiLogsQuery.joinApiInfo(apiLogs);
        return batchInsert(apiLogs);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void clearOperationLog(Integer clearBeforeDay) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        LocalDateTime delDate = LocalDateTime.now().minusDays(clearBeforeDay);
        apiLogsRepo.deleteByRequestDateBefore(delDate);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<ApiLog, Long> getRepository() {
    return apiLogsRepo;
  }

}
