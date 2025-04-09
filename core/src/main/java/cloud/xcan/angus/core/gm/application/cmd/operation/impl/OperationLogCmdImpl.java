package cloud.xcan.angus.core.gm.application.cmd.operation.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

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
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Biz
public class OperationLogCmdImpl extends CommCmd<OperationLog, Long> implements OperationLogCmd {

  @Resource
  private OperationLogRepo operationLogRepo;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void add(OperationLog operation) {
    if (nonNull(operation) && (isUserAction() || isNotEmpty(operation.getFullName()))) {
      insert0(operation);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void addAll(List<OperationLog> operations) {
    if (isEmpty(operations) && (isUserAction() || isNotEmpty(operations.get(0).getFullName()))) {
      batchInsert0(operations);
    }
  }

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
