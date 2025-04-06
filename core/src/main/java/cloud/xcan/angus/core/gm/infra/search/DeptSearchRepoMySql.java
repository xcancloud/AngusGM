package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.core.gm.domain.dept.DeptListRepo;
import cloud.xcan.angus.core.gm.domain.dept.DeptSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class DeptSearchRepoMySql extends SimpleSearchRepository<Dept> implements DeptSearchRepo {

  @Resource
  private DeptListRepo deptListRepo;

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<Dept> mainClz,
      Object[] params, String... matches) {
    return deptListRepo.getSqlTemplate0(getSearchMode(), criteria, mainClz, "dept", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return deptListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return super.getSearchMode();
  }
}
