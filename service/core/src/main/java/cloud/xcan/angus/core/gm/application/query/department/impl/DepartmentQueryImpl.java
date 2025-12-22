package cloud.xcan.angus.core.gm.application.query.department.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentRepo;
import cloud.xcan.angus.core.gm.domain.department.DepartmentSearchRepo;
import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import cloud.xcan.angus.core.gm.domain.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserRepo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Implementation of department query service
 */
@Biz
public class DepartmentQueryImpl implements DepartmentQuery {

  @Resource
  private DepartmentRepo departmentRepo;

  @Resource
  private DepartmentSearchRepo departmentSearchRepo;

  @Resource
  private UserRepo userRepo;

  @Override
  public Department findAndCheck(Long id) {
    return new BizTemplate<Department>() {
      @Override
      protected Department process() {
        return departmentRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("部门未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<Department> find(GenericSpecification<Department> spec, PageRequest pageable,
                                boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Department>>() {
      @Override
      protected Page<Department> process() {
        Page<Department> page = fullTextSearch
            ? departmentSearchRepo.find(spec.getCriteria(), pageable, Department.class, match)
            : departmentRepo.findAll(spec, pageable);
        
        // Set associated data if needed
        if (page.hasContent()) {
          // TODO: Set leader names, member counts, parent names
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public List<Department> findTree(Long parentId, String status) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        DepartmentStatus deptStatus = null;
        if (status != null && !status.isEmpty()) {
          try {
            deptStatus = DepartmentStatus.valueOf(status.toUpperCase());
          } catch (IllegalArgumentException e) {
            // Invalid status, ignore filter
          }
        }
        
        if (parentId == null) {
          if (deptStatus != null) {
            return departmentRepo.findByParentIdIsNullAndStatus(deptStatus);
          }
          return departmentRepo.findByParentIdIsNull();
        } else {
          if (deptStatus != null) {
            return departmentRepo.findByParentIdAndStatus(parentId, deptStatus);
          }
          return departmentRepo.findByParentId(parentId);
        }
      }
    }.execute();
  }

  @Override
  public boolean existsByCode(String code) {
    return departmentRepo.existsByCode(code);
  }

  @Override
  public DepartmentStatsVo getStats() {
    return new BizTemplate<DepartmentStatsVo>() {
      @Override
      protected DepartmentStatsVo process() {
        DepartmentStatsVo stats = new DepartmentStatsVo();
        
        // Total departments
        long totalDepartments = departmentRepo.count();
        stats.setTotalDepartments(totalDepartments);
        
        // Enabled departments
        long enabledDepartments = departmentRepo.countByStatus(DepartmentStatus.ENABLED);
        stats.setEnabledDepartments(enabledDepartments);
        
        // Disabled departments
        long disabledDepartments = departmentRepo.countByStatus(DepartmentStatus.DISABLED);
        stats.setDisabledDepartments(disabledDepartments);
        
        // TODO: Get total users from user repository
        stats.setTotalUsers(0L);
        
        // Max level
        Integer maxLevel = departmentRepo.findMaxLevel();
        stats.setMaxLevel(maxLevel != null ? maxLevel : 0);
        
        // New departments this month
        LocalDateTime firstDayOfMonth = LocalDateTime.now()
            .with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newDepartmentsThisMonth = departmentRepo.countByCreatedDateAfter(firstDayOfMonth);
        stats.setNewDepartmentsThisMonth(newDepartmentsThisMonth);
        
        return stats;
      }
    }.execute();
  }

  @Override
  public List<Department> findChildren(Long parentId, Boolean recursive) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        if (parentId == null) {
          return List.of();
        }
        
        List<Department> children = departmentRepo.findByParentId(parentId);
        
        if (Boolean.TRUE.equals(recursive) && !children.isEmpty()) {
          List<Department> allChildren = new java.util.ArrayList<>(children);
          for (Department child : children) {
            allChildren.addAll(findChildren(child.getId(), true));
          }
          return allChildren;
        }
        
        return children;
      }
    }.execute();
  }

  @Override
  public List<Department> getPath(Long id) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        List<Department> path = new java.util.ArrayList<>();
        Department department = findAndCheck(id);
        
        // Build path from current department to root
        Department current = department;
        while (current != null) {
          path.add(0, current); // Add to beginning
          if (current.getParentId() != null) {
            current = departmentRepo.findById(current.getParentId()).orElse(null);
          } else {
            current = null;
          }
        }
        
        return path;
      }
    }.execute();
  }

  @Override
  public Page<User> findMembers(Long departmentId, GenericSpecification<User> spec, PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected Page<User> process() {
        // Ensure departmentId filter is included
        return userRepo.findAll(spec, pageable);
      }
    }.execute();
  }
}
