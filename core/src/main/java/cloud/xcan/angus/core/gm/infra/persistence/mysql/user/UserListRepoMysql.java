package cloud.xcan.angus.core.gm.infra.persistence.mysql.user;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.getFilterInFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.safeStringInValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.user.UserListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class UserListRepoMysql extends AbstractSearchRepository<User> implements UserListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<User> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "user0", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<User> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder("SELECT %s FROM " + tableName + " " + mainAlis);

    // Assemble non mainClz tagIds in Conditions
    sql.append(assembleTargetTagJoinCondition(criteria, OrgTargetType.USER))
        .append(" WHERE ").append(mainAlis).append(".deleted = 0 ")
        // Assemble mainClz Conditions
        .append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false, matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }

  public static StringBuilder assembleTargetTagJoinCondition(Set<SearchCriteria> criteria,
      OrgTargetType targetType) {
    StringBuilder sql = new StringBuilder();
    String tagIdInValue = getFilterInFirstValue(criteria, "tagId");
    String tagIdEqualValue = findFirstValue(criteria, "tagId", SearchOperation.EQUAL);
    if (isEmpty(tagIdInValue) && isEmpty(tagIdEqualValue)) {
      return sql;
    }
    sql.append(" INNER JOIN org_tag_target tag ON a.id = tag.target_id AND tag.target_type = '")
        .append(targetType.getValue()).append("'");
    if (isNotBlank(tagIdInValue)) {
      tagIdInValue = safeStringInValue(tagIdEqualValue);
      sql.append(" AND tag.tag_id IN (").append(tagIdInValue).append(")");
    }
    if (isNotBlank(tagIdEqualValue)) {
      sql.append(" AND tag.tag_id = ").append(tagIdEqualValue);
    }
    return sql;
  }

}
