package cloud.xcan.angus.core.gm.infra.persistence.mysql.dept;


import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.remote.search.SearchOperation.EQUAL;
import static cloud.xcan.angus.remote.search.SearchOperation.MATCH_END;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.core.gm.domain.dept.user.DeptUserListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.utils.StringUtils;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class DeptUserListRepoMysql extends AbstractSearchRepository<DeptUser> implements
    DeptUserListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<DeptUser> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "dept_user", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode,
      Set<SearchCriteria> criteria, Class<DeptUser> mainClz, String tableName,
      String... matches) {
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
    return "a.id, a.tenant_id, a.dept_head, a.main_dept ,a.created_by, a.created_date, t.id deptId, t.name deptName, t.code deptCode, org.id userId, org.full_name fullName, org.avatar, org.mobile";
  }

  public static StringBuilder assembleTagJoinCondition(Set<SearchCriteria> criteria) {
    StringBuilder sql = new StringBuilder();
    String deptIdEqualValue = findFirstValueAndRemove(criteria, "deptId", EQUAL);
    String deptNameEqualValue = findFirstValueAndRemove(criteria, "deptName", EQUAL);
    String deptNameMatchValue = findFirstValueAndRemove(criteria, "deptName", MATCH_END);
    String userIdEqualValue = findFirstValueAndRemove(criteria, "userId", EQUAL);
    String fullNameEqualValue = findFirstValueAndRemove(criteria, "fullName", EQUAL);
    String fullNameMatchValue = findFirstValueAndRemove(criteria, "fullName", MATCH_END);
    Long tenantId = getOptTenantId();
    sql.append(" INNER JOIN dept t ON a.dept_id = t.id ")
        .append(" AND t.tenant_id = ").append(tenantId);
    if (StringUtils.isNotBlank(deptIdEqualValue)) {
      sql.append(" AND t.id = ").append(deptIdEqualValue);
    }
    if (StringUtils.isNotBlank(deptNameEqualValue)) {
      sql.append(" AND t.name = ").append(deptNameEqualValue);
    }
    if (StringUtils.isNotBlank(deptNameMatchValue)) {
      sql.append(" AND t.name like '").append(deptNameMatchValue).append("%'");
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
