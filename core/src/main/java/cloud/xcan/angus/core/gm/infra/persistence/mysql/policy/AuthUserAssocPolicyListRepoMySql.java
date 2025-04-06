package cloud.xcan.angus.core.gm.infra.persistence.mysql.policy;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.getFilterInFirstValue;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.remote.search.SearchOperation.EQUAL;
import static com.alibaba.excel.util.StringUtils.isEmpty;
import static java.lang.Boolean.parseBoolean;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthUserAssocPolicyListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class AuthUserAssocPolicyListRepoMySql extends AbstractSearchRepository<AuthPolicy>
    implements AuthUserAssocPolicyListRepo {

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

    String isSysAdmin = findFirstValue(criteria, "isSysAdmin", EQUAL);
    assertNotEmpty(isSysAdmin, "Parameter isSysAdmin is required");
    String adminFullAssociated = findFirstValue(criteria, "adminFullAssociated");
    if (!Boolean.parseBoolean(isSysAdmin) || isEmpty(adminFullAssociated)
        || !parseBoolean(adminFullAssociated)) {
      sql.append(assembleJoinAuthOrg(criteria));
    }

    // Assemble non mainClz orgId in Conditions
    sql.append(" WHERE a.app_id IN (SELECT app_id FROM app_open WHERE tenant_id = ")
        .append(optTenantId)
        .append(" AND edition_type = '").append(getApplicationInfo().getEditionType())
        .append("')");
    // Assemble mainClz Conditions
    //.append(getCriteriaAliasCondition(step, criteria, mainAlis, mode, false, matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    String ignoreAuthOrg = findFirstValue(criteria, "ignoreAuthOrg");
    if (isNotEmpty(ignoreAuthOrg) && parseBoolean(ignoreAuthOrg)) {
      return "DISTINCT a.*";
    }
    return "DISTINCT a.*, po.org_type orgType, po.org_id orgId, po.grant_scope grantScope, po.default0 currentDefault0, po.open_auth openAuth, po.created_by authBy, po.created_date authDate";
  }

  @Override
  public String getReturnCountCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "count(DISTINCT a.id)";
  }

  public static StringBuilder assembleJoinAuthOrg(Set<SearchCriteria> criteria) {
    StringBuilder sql = new StringBuilder();
    String allOrgIdInValue = getFilterInFirstValue(criteria, "orgId");
    assertNotEmpty(allOrgIdInValue, "Parameter orgId is required");
    String isSysAdmin = findFirstValueAndRemove(criteria, "isSysAdmin");
    assertNotEmpty(isSysAdmin, "Parameter isSysAdmin is required");
    String orgIdsInValue = getInConditionValue(allOrgIdInValue);
    Long tenantId = getOptTenantId();
    sql.append(" INNER JOIN auth_policy_org po ON a.id = po.policy_id AND po.tenant_id = ")
        .append(tenantId).append(" AND po.org_id IN").append(orgIdsInValue);

    sql.append(" AND (po.org_type <> 'TENANT' ").append(parseBoolean(isSysAdmin)
        // Tenant default and opened authorization are included when the system administrator
        ? "OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR po.open_auth =1)))"
        // Tenant default and opened authorization are included when the system administrator
        : "OR (po.org_type = 'TENANT' AND (po.default0 = 1 OR (po.grant_scope = 'TENANT_ALL_USER' AND po.open_auth =1))))");
    return sql;
  }

  public static String getInConditionValue(String authObjectIds) {
    return "(" + authObjectIds + ")";
  }

  public static String getInConditionValue(Set<String> authObjectIds) {
    return "(" + String.join(",", authObjectIds) + ")";
  }
}
