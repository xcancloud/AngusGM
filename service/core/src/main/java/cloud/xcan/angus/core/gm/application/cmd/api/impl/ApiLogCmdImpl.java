package cloud.xcan.angus.core.gm.application.cmd.api.impl;


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

/**
 * Implementation of API log command operations for managing API request logs.
 *
 * <p>This class provides functionality for:</p>
 * <ul>
 *   <li>Storing API request logs for analysis and monitoring</li>
 *   <li>Cleaning up old log records based on retention policies</li>
 *   <li>Joining API information with log records for comprehensive reporting</li>
 * </ul>
 *
 * <p>Note: Future implementation will include sharding by tenant for better scalability.</p>
 */
@DoInFuture("Sharding by tenant")
@Slf4j
@org.springframework.stereotype.Service
public class ApiLogCmdImpl extends CommCmd<ApiLog, Long> implements ApiLogCmd {

  @Resource
  private ApiLogRepo apiLogsRepo;
  @Resource
  private ApiLogsQuery apiLogsQuery;

  /**
   * Adds API request logs to the database with associated API information.
   *
   * <p>This method performs the following operations:</p>
   * <ul>
   *   <li>Joins API information with log records for comprehensive reporting</li>
   *   <li>Batch inserts log records for optimal performance</li>
   *   <li>Returns identifiers for created log entries</li>
   * </ul>
   *
   * @param apiLogs List of API log entities to store
   * @return List of created log identifiers with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<ApiLog> apiLogs) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected List<IdKey<Long, Object>> process() {
        // Join API information with log records for comprehensive reporting
        apiLogsQuery.joinApiInfo(apiLogs);
        return batchInsert(apiLogs);
      }
    }.execute();
  }

  /**
   * Cleans up old API operation logs based on retention policy.
   *
   * <p>This method removes log records older than the specified number of days
   * to maintain database performance and comply with data retention policies.</p>
   *
   * @param clearBeforeDay Number of days before current date to retain logs
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void clearOperationLog(Integer clearBeforeDay) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Calculate cutoff date for log cleanup
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
