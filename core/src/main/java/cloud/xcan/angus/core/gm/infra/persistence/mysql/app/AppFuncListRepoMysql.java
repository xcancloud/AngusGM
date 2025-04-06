package cloud.xcan.angus.core.gm.infra.persistence.mysql.app;

import static cloud.xcan.angus.core.gm.infra.persistence.mysql.app.AppListRepoMysql.assembleTagAndApiJoinCondition;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.app.func.AppFuncListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class AppFuncListRepoMysql extends AbstractSearchRepository<AppFunc> implements
    AppFuncListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<AppFunc> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "app_func", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<AppFunc> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder("SELECT %s FROM " + tableName + " " + mainAlis);

    // Assemble non mainClz tagIds in Conditions
    sql.append(assembleTagAndApiJoinCondition(criteria))
        .append(" WHERE 1=1 ")
        // Assemble mainClz Conditions
        .append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false, matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }

}
