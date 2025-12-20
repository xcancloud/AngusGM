package cloud.xcan.angus.core.gm.application.query.department.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.domain.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
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
        Page<Department> page = departmentRepo.findAll(spec, pageable);
        
        // Set associated data if needed
        if (page.hasContent()) {
          // TODO: Set leader names, member counts, parent names
        }
        
        return page;
      }
    }.execute();
  }

  @Override
  public List<Department> findTree(Long parentId) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        if (parentId == null) {
          return departmentRepo.findByParentIdIsNull();
        } else {
          return departmentRepo.findByParentId(parentId);
        }
      }
    }.execute();
  }

  @Override
  public boolean existsByCode(String code) {
    return departmentRepo.existsByCode(code);
  }
}
