package cloud.xcan.angus.core.gm.application.cmd.operation.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationLogRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Biz
public class OperationLogCmdImpl extends CommCmd<OperationLog, Long> implements OperationLogCmd {

  @Resource
  private OperationLogRepo operationLogRepo;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<OperationLog> operationLogs) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {

      @Override
      protected List<IdKey<Long, Object>> process() {
        return batchInsert(operationLogs);
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
