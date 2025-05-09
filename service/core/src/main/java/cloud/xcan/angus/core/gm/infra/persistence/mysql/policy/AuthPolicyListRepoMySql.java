package cloud.xcan.angus.core.gm.infra.persistence.mysql.policy;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;

import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicyListRepo;
import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;


@Repository
public class AuthPolicyListRepoMySql extends AbstractSearchRepository<AuthPolicy> implements
    AuthPolicyListRepo {

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
    // Query the policy of authorization to other tenants
    StringBuilder sql = new StringBuilder(
        "SELECT %s FROM (SELECT * FROM auth_policy a1 WHERE a1.tenant_id = " + optTenantId
            + getCriteriaAliasCondition(criteria, mainClz, "a1", mode, false, matches)
            + " UNION SELECT * FROM auth_policy a2 WHERE type = 'PRE_DEFINED' "
            + getCriteriaAliasCondition(criteria, mainClz, "a2", mode, false, matches) + ") "
            + mainAlis);
    if (!isOpClient()) {
      // Assemble non mainClz tagIds in Conditions
      sql.append(" WHERE ")
          .append(mainAlis).append(".client_id = '").append(getClientId()).append("'");
      // Only allowed to query opened application policies
      // Important:: Operating tenants need to open all applications first
      sql.append(" AND ").append(mainAlis)
          .append(".app_id IN (SELECT app_id FROM app_open WHERE tenant_id = ").append(optTenantId)
          // Fix:: Excluding private editions applications and authorizations for cloud service edition
          .append(" AND edition_type = '").append(getApplicationInfo().getEditionType())
          .append("')");
    }
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }
}
