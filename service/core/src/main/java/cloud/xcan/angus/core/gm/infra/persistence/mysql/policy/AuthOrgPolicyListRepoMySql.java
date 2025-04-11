package cloud.xcan.angus.core.gm.infra.persistence.mysql.policy;

import static cloud.xcan.angus.core.gm.infra.persistence.mysql.policy.AuthUserAssocPolicyListRepoMySql.getInConditionValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findAllIdInAndEqualValues;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.remote.search.SearchOperation.EQUAL;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthOrgPolicyListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class AuthOrgPolicyListRepoMySql extends AbstractSearchRepository<AuthPolicy> implements
    AuthOrgPolicyListRepo {

  /**
   * Non-main mainClz conditions and joins need to be assembled by themselves
   */
  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<AuthPolicy> mainClz,
      Object[] params, String... matches) {
    return getSqlTemplate0(getSearchMode(), criteria, mainClz, "auth_policy", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Set<SearchCriteria> criteria,
      Class<AuthPolicy> mainClz, String tableName, String... matches) {
    String mainAlis = "a";
    Long optTenantId = getOptTenantId();
    // Assemble mainClz table
    StringBuilder sql = new StringBuilder(
        "SELECT %s FROM (SELECT * FROM auth_policy a1 WHERE a1.tenant_id = " + optTenantId
            + getCriteriaAliasCondition(criteria, mainClz, "a1", mode, false, matches)
            + " UNION SELECT * FROM auth_policy a2 WHERE type = 'PRE_DEFINED' "
            + getCriteriaAliasCondition(criteria, mainClz, "a2", mode, false, matches) + ") "
            + mainAlis);

    // String isSysAdminEqualValue = findFirstValue(criteria, "isSysAdmin", EQUAL);
    // ProtocolAssert.assertNotEmpty(isSysAdminEqualValue, "Parameter isSysAdmin is required");
    // String adminFullAssociated = findFirstValue(criteria, "adminFullAssociated");
    // if (!Boolean.parseBoolean(isSysAdminEqualValue) || isEmpty(adminFullAssociated)
    //     || !parseBoolean(adminFullAssociated)){
    //   sql.append(assembleOrgAuthJoinCondition(criteria));
    // }

    sql.append(assembleOrgAuthJoinCondition(criteria));

    // Assemble non mainClz orgId in Conditions
    sql.append(" WHERE 1=1 ");
    // Assemble mainClz Conditions
    //.append(getCriteriaAliasCondition(step, criteria, mainAlis, mode, false, matches));
    // Only allowed to query opened application policies
    // Important:: Operating tenants need to open all applications first
    sql.append(" AND a.app_id IN (SELECT app_id FROM app_open WHERE tenant_id = ")
        .append(optTenantId)
        .append(" AND edition_type = '").append(getApplicationInfo().getEditionType())
        .append("')");
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    String ignoreAuthOrg = findFirstValueAndRemove(criteria, "ignoreAuthOrg");
    if (isNotEmpty(ignoreAuthOrg) && Boolean.parseBoolean(ignoreAuthOrg)) {
      return "DISTINCT a.*";
    }
    return "DISTINCT a.*, po.created_by authBy, po.created_date authDate";
  }

  @Override
  public String getReturnCountCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "count(DISTINCT a.id)";
  }

  public static StringBuilder assembleOrgAuthJoinCondition(Set<SearchCriteria> criteria) {
    StringBuilder sql = new StringBuilder();
    String orgTypeEqualValue = findFirstValueAndRemove(criteria, "orgType", EQUAL);
    ProtocolAssert.assertNotEmpty(orgTypeEqualValue, "Parameter orgType is required");

    if (orgTypeEqualValue.equals(AuthOrgType.USER.getValue())) {
      Set<String> orgIdsInValue = findAllIdInAndEqualValues(criteria, "orgId", true);
      ProtocolAssert.assertNotEmpty(orgIdsInValue, "Parameter orgId is required");
      String isSysAdminEqualValue = findFirstValue(criteria, "isSysAdmin", EQUAL);
      ProtocolAssert.assertNotEmpty(isSysAdminEqualValue, "Parameter isSysAdmin is required");

      sql.append(" INNER JOIN auth_policy_org po ON a.id = po.policy_id AND po.org_id IN ")
          .append(getInConditionValue(orgIdsInValue));

      sql.append(" AND (po.org_type <> 'TENANT' ").append(Boolean.parseBoolean(isSysAdminEqualValue)
          // Tenant default and opened authorization are included when the system administrator
          ? "OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR po.open_auth =1)))"
          // Tenant default and opened authorization are included when the system administrator
          : "OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR (po.grant_scope = 'TENANT_ALL_USER' AND po.open_auth =1))))");

      return sql;
    } else {
      String orgIdEqualValue = findFirstValueAndRemove(criteria, "orgId", EQUAL);
      ProtocolAssert.assertNotEmpty(orgIdEqualValue, "Parameter orgId is required");
      return sql.append(" INNER JOIN auth_policy_org po ON a.id = po.policy_id AND po.org_id = ")
          .append(orgIdEqualValue).append(" AND po.org_type = '").append(orgTypeEqualValue)
          .append("'");
    }
  }
}
