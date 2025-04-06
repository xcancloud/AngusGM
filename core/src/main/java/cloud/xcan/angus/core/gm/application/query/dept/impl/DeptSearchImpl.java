package cloud.xcan.angus.core.gm.application.query.dept.impl;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.gm.application.query.dept.DeptSearch;
import cloud.xcan.angus.core.gm.domain.dept.DeptSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Biz
public class DeptSearchImpl implements DeptSearch {

  @Resource
  private DeptSearchRepo deptSearchRepo;

  @Resource
  private DeptQuery deptQuery;

  @Override
  public Page<Dept> search(Set<SearchCriteria> criteria, Pageable pageable,
      Class<Dept> clz, String... matches) {
    return new BizTemplate<Page<Dept>>(true, true) {

      @Override
      protected Page<Dept> process() {
        Page<Dept> deptPage = deptSearchRepo.find(criteria, pageable, clz, matches);
        deptQuery.setHasSubDept(deptPage.getContent());
        return deptPage;
      }
    }.execute();
  }
}
