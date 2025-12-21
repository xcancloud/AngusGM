package cloud.xcan.angus.core.gm.application.query.auditlog.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.auditlog.AuditLogQuery;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLog;
import cloud.xcan.angus.core.gm.domain.auditlog.AuditLogRepo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogStatsVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.ModuleStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Audit log query service implementation
 * </p>
 */
@Service
public class AuditLogQueryImpl implements AuditLogQuery {

  @Resource
  private AuditLogRepo auditLogRepo;

  @Override
  public AuditLog findAndCheck(Long id) {
    return new BizTemplate<AuditLog>() {
      @Override
      protected AuditLog process() {
        return auditLogRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("审计日志不存在", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<AuditLog> find(GenericSpecification<AuditLog> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuditLog>>() {
      @Override
      protected Page<AuditLog> process() {
        return auditLogRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<AuditLog> findByUserId(Long userId, GenericSpecification<AuditLog> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuditLog>>() {
      @Override
      protected Page<AuditLog> process() {
        return auditLogRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<AuditLog> findSensitiveLogs(GenericSpecification<AuditLog> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuditLog>>() {
      @Override
      protected Page<AuditLog> process() {
        return auditLogRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<AuditLog> findFailureLogs(GenericSpecification<AuditLog> spec, PageRequest pageable) {
    return new BizTemplate<Page<AuditLog>>() {
      @Override
      protected Page<AuditLog> process() {
        return auditLogRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public AuditLogStatsVo getStats(LocalDateTime startDate, LocalDateTime endDate) {
    return new BizTemplate<AuditLogStatsVo>() {
      @Override
      protected AuditLogStatsVo process() {
        AuditLogStatsVo stats = new AuditLogStatsVo();
        
        // Set period
        AuditLogStatsVo.Period period = new AuditLogStatsVo.Period();
        period.setStartDate(startDate.toLocalDate().toString());
        period.setEndDate(endDate.toLocalDate().toString());
        stats.setPeriod(period);
        
        // Total logs
        long totalLogs = auditLogRepo.countByOperationTimeBetween(startDate, endDate);
        stats.setTotalLogs(totalLogs);
        
        // Count by level
        List<Object[]> levelCounts = auditLogRepo.countByLevelGroupBy(startDate, endDate);
        Map<String, Long> byLevel = new LinkedHashMap<>();
        for (Object[] row : levelCounts) {
          String level = (String) row[0];
          Long count = (Long) row[1];
          byLevel.put(level != null ? level : "unknown", count);
        }
        stats.setByLevel(byLevel);
        
        // Count by module
        List<Object[]> moduleCounts = auditLogRepo.countByModuleGroupBy(startDate, endDate);
        Map<String, Long> byModule = new LinkedHashMap<>();
        for (Object[] row : moduleCounts) {
          String module = (String) row[0];
          Long count = (Long) row[1];
          byModule.put(module != null ? module : "other", count);
        }
        stats.setByModule(byModule);
        
        // Top users (top 10)
        List<Object[]> userCounts = auditLogRepo.countByUserGroupBy(startDate, endDate, PageRequest.of(0, 10));
        List<AuditLogStatsVo.TopUser> topUsers = new ArrayList<>();
        for (Object[] row : userCounts) {
          AuditLogStatsVo.TopUser topUser = new AuditLogStatsVo.TopUser();
          topUser.setUserId(String.valueOf(row[0]));
          topUser.setUserName((String) row[1]);
          topUser.setOperationCount((Long) row[2]);
          topUsers.add(topUser);
        }
        stats.setTopUsers(topUsers);
        
        return stats;
      }
    }.execute();
  }

  @Override
  public List<ModuleStatsVo> getModuleStats(LocalDateTime startDate, LocalDateTime endDate) {
    return new BizTemplate<List<ModuleStatsVo>>() {
      @Override
      protected List<ModuleStatsVo> process() {
        List<Object[]> moduleOperationCounts = auditLogRepo.countByModuleAndOperationGroupBy(startDate, endDate);
        
        // Group by module
        Map<String, ModuleStatsVo> moduleStatsMap = new LinkedHashMap<>();
        
        for (Object[] row : moduleOperationCounts) {
          String module = (String) row[0];
          String operation = (String) row[1];
          Long count = (Long) row[2];
          
          String moduleKey = module != null ? module : "other";
          
          ModuleStatsVo stats = moduleStatsMap.computeIfAbsent(moduleKey, k -> {
            ModuleStatsVo vo = new ModuleStatsVo();
            vo.setModule(moduleKey);
            vo.setModuleName(getModuleName(moduleKey));
            vo.setTotalOperations(0L);
            vo.setCreate(0L);
            vo.setUpdate(0L);
            vo.setDelete(0L);
            vo.setQuery(0L);
            return vo;
          });
          
          stats.setTotalOperations(stats.getTotalOperations() + count);
          
          if (operation != null) {
            switch (operation.toLowerCase()) {
              case "create":
                stats.setCreate(stats.getCreate() + count);
                break;
              case "update":
                stats.setUpdate(stats.getUpdate() + count);
                break;
              case "delete":
                stats.setDelete(stats.getDelete() + count);
                break;
              case "query":
              case "list":
              case "get":
              case "find":
                stats.setQuery(stats.getQuery() + count);
                break;
            }
          }
        }
        
        return new ArrayList<>(moduleStatsMap.values());
      }
    }.execute();
  }

  /**
   * Get module display name
   */
  private String getModuleName(String module) {
    if (module == null) {
      return "其他";
    }
    Map<String, String> moduleNames = new HashMap<>();
    moduleNames.put("users", "用户管理");
    moduleNames.put("tenants", "租户管理");
    moduleNames.put("permissions", "权限管理");
    moduleNames.put("applications", "应用管理");
    moduleNames.put("roles", "角色管理");
    moduleNames.put("departments", "部门管理");
    moduleNames.put("groups", "组织管理");
    moduleNames.put("policies", "策略管理");
    moduleNames.put("auditlog", "审计日志");
    moduleNames.put("other", "其他");
    return moduleNames.getOrDefault(module.toLowerCase(), module);
  }
}

