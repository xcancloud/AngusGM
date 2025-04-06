package cloud.xcan.angus.core.gm.infra.persistence.mysql.app;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.getFilterInFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.safeStringInValue;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppListRepo;
import cloud.xcan.angus.core.jpa.criteria.CriteriaUtils;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class AppListRepoMysql extends AbstractSearchRepository<App> implements AppListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<App> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "app", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<App> mainClz, String tableName, String... matches) {
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

  public static StringBuilder assembleTagAndApiJoinCondition(Set<SearchCriteria> criteria) {
    StringBuilder sql = new StringBuilder();
    String tagIdInValue = getFilterInFirstValue(criteria, "tagId");
    String tagIdEqualValue = CriteriaUtils.findFirstValue(criteria, "tagId",
        SearchOperation.EQUAL);
    if (isNotEmpty(tagIdInValue) || isNotEmpty(tagIdEqualValue)) {
      sql.append(" INNER JOIN web_tag_target tag ON a.id = tag.target_id");
      if (isNotBlank(tagIdInValue)) {
        tagIdInValue = safeStringInValue(tagIdInValue);
        sql.append(" AND tag.tag_id IN (").append(tagIdInValue).append(")");
      }
      if (isNotBlank(tagIdEqualValue)) {
        sql.append(" AND tag.tag_id = ").append(tagIdEqualValue);
      }
    }
    String apiIdInValue = getFilterInFirstValue(criteria, "apiId");
    String apiIdEqualValue = CriteriaUtils.findFirstValue(criteria, "apiId",
        SearchOperation.EQUAL);
    if (isNotEmpty(apiIdInValue) || isNotEmpty(apiIdEqualValue)) {
      sql.append(" INNER JOIN authority tag ON a.id = auth.source_id");
      if (isNotBlank(apiIdInValue)) {
        apiIdInValue = safeStringInValue(apiIdInValue);
        sql.append(" AND auth.app_id IN (").append(apiIdInValue).append(")");
      }
      if (isNotBlank(apiIdEqualValue)) {
        sql.append(" AND auth.app_id = ").append(apiIdEqualValue);
      }
    }
    return sql;
  }
}
