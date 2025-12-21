package cloud.xcan.angus.core.gm.application.cmd.department.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentRepo;
import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of department command service
 */
@Biz
public class DepartmentCmdImpl extends CommCmd<Department, Long> implements DepartmentCmd {

  @Resource
  private DepartmentRepo departmentRepo;

  @Resource
  private DepartmentQuery departmentQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Department create(Department department) {
    return new BizTemplate<Department>() {
      @Override
      protected void checkParams() {
        if (departmentRepo.existsByCode(department.getCode())) {
          throw ResourceExisted.of("部门编码「{0}」已存在", new Object[]{department.getCode()});
        }
        
        // Calculate level based on parent
        if (department.getParentId() != null) {
          Department parent = departmentQuery.findAndCheck(department.getParentId());
          department.setLevel(parent.getLevel() + 1);
        } else {
          department.setLevel(1);
        }
      }

      @Override
      protected Department process() {
        if (department.getStatus() == null) {
          department.setStatus(DepartmentStatus.ENABLED);
        }
        if (department.getSortOrder() == null) {
          department.setSortOrder(0);
        }
        insert(department);
        return department;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Department update(Department department) {
    return new BizTemplate<Department>() {
      Department departmentDb;

      @Override
      protected void checkParams() {
        departmentDb = departmentQuery.findAndCheck(department.getId());
        
        if (department.getCode() != null && !department.getCode().equals(departmentDb.getCode())) {
          if (departmentRepo.existsByCodeAndIdNot(department.getCode(), department.getId())) {
            throw ResourceExisted.of("部门编码「{0}」已存在", new Object[]{department.getCode()});
          }
        }
        
        // Recalculate level if parent changed
        if (department.getParentId() != null && !department.getParentId().equals(departmentDb.getParentId())) {
          Department parent = departmentQuery.findAndCheck(department.getParentId());
          department.setLevel(parent.getLevel() + 1);
        }
      }

      @Override
      protected Department process() {
        update(department, departmentDb);
        return departmentDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        departmentQuery.findAndCheck(id);
        
        // Check if has children
        long childCount = departmentRepo.countByParentId(id);
        if (childCount > 0) {
          throw ResourceExisted.of("部门下存在子部门，无法删除", new Object[]{});
        }
      }

      @Override
      protected Void process() {
        departmentRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void enable(Long id) {
    new BizTemplate<Void>() {
      Department departmentDb;

      @Override
      protected void checkParams() {
        departmentDb = departmentQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        departmentDb.setStatus(DepartmentStatus.ENABLED);
        departmentRepo.save(departmentDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      Department departmentDb;

      @Override
      protected void checkParams() {
        departmentDb = departmentQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        departmentDb.setStatus(DepartmentStatus.DISABLED);
        departmentRepo.save(departmentDb);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Department, Long> getRepository() {
    return departmentRepo;
  }
}
