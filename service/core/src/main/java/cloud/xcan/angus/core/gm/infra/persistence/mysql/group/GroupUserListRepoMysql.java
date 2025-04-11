package cloud.xcan.angus.core.gm.infra.persistence.mysql.group;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.remote.search.SearchOperation.EQUAL;
import static cloud.xcan.angus.remote.search.SearchOperation.MATCH_END;

import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.core.gm.domain.group.user.GroupUserListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.utils.StringUtils;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class GroupUserListRepoMysql extends AbstractSearchRepository<GroupUser> implements
    GroupUserListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<GroupUser> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "group_user", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<GroupUser> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder("SELECT DISTINCT %s FROM " + tableName + " " + mainAlis);

    // Assemble non mainClz tagIds in Conditions
    sql.append(assembleTagJoinCondition(criteria))
        .append(" WHERE 1=1 ")
        // Assemble mainClz Conditions: createdBy, createdDate
        .append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false,
            matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.id, a.tenant_id, a.created_by, a.created_date, t.id groupId, t.name groupName, t.code groupCode, "
        + "t.enabled groupEnabled, t.remark, org.id userId, org.full_name fullName, org.avatar, org.mobile";
  }

  public static StringBuilder assembleTagJoinCondition(Set<SearchCriteria> criteria) {
    StringBuilder sql = new StringBuilder();
    String groupIdEqualValue = findFirstValueAndRemove(criteria, "groupId", EQUAL);
    String groupNameEqualValue = findFirstValueAndRemove(criteria, "groupName", EQUAL);
    String groupNameMatchValue = findFirstValueAndRemove(criteria, "groupName", MATCH_END);
    String userIdEqualValue = findFirstValueAndRemove(criteria, "userId", EQUAL);
    String fullNameEqualValue = findFirstValueAndRemove(criteria, "fullName", EQUAL);
    String fullNameMatchValue = findFirstValueAndRemove(criteria, "fullName", MATCH_END);
    Long tenantId = getOptTenantId();
    sql.append(" INNER JOIN group0 t ON a.group_id = t.id ")
        .append(" AND t.tenant_id = ").append(tenantId);
    if (StringUtils.isNotBlank(groupIdEqualValue)) {
      sql.append(" AND t.id = ").append(groupIdEqualValue);
    }
    if (StringUtils.isNotBlank(groupNameEqualValue)) {
      sql.append(" AND t.name = ").append(groupNameEqualValue);
    }
    if (StringUtils.isNotBlank(groupNameMatchValue)) {
      sql.append(" AND t.name like '").append(groupNameMatchValue).append("%'");
    }
    sql.append(" INNER JOIN user0 org ON a.user_id = org.id ")
        .append(" AND org.tenant_id = ").append(tenantId);
    if (StringUtils.isNotBlank(userIdEqualValue)) {
      sql.append(" AND org.id = ").append(userIdEqualValue);
    }
    if (StringUtils.isNotBlank(fullNameEqualValue)) {
      sql.append(" AND org.full_name = ").append(fullNameEqualValue);
    }
    if (StringUtils.isNotBlank(fullNameMatchValue)) {
      sql.append(" AND org.full_name like '").append(fullNameMatchValue).append("%'");
    }
    return sql;
  }
}
