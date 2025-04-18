package cloud.xcan.angus.core.gm.application.query.policy.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.gm.application.query.policy.impl.AuthPolicyUserQueryImpl.getIgnoreAuthOrg;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValueAndRemove;
import static cloud.xcan.angus.remote.search.SearchOperation.EQUAL;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.dept.DeptRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyDeptQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyUserQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthOrgPolicyListRepo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;


@Biz
public class AuthPolicyDeptQueryImpl implements AuthPolicyDeptQuery {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AuthOrgPolicyListRepo authOrgPolicyListRepo;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private DeptRepo deptRepo;

  @Resource
  private AuthPolicyUserQuery authPolicyUserQuery;

  @Override
  public Page<Dept> policyDeptList(GenericSpecification<Dept> spec, PageRequest pageable) {
    return new BizTemplate<Page<Dept>>() {
      String policyId;

      @Override
      protected void checkParams() {
        policyId = findFirstValueAndRemove(spec.getCriteria(), "policyId", EQUAL);
        assertNotEmpty(policyId, "Parameter policyId is required");
        closeMultiTenantCtrl(); // May be default policy
        authPolicyQuery.checkAndFind(Long.valueOf(policyId), false, false);
        enableMultiTenantCtrl();
      }

      @Override
      protected Page<Dept> process() {
        List<Long> deptIds = authPolicyOrgRepo.finOrgIdsByPolicyIdAndOrgType(
            Long.valueOf(policyId), AuthOrgType.DEPT.getValue());
        if (isEmpty(deptIds)) {
          return Page.empty();
        }
        spec.getCriteria().add(SearchCriteria.in("id", deptIds));
        return deptRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<Dept> policyUnauthDeptList(GenericSpecification<Dept> spec, PageRequest pageable) {
    return new BizTemplate<Page<Dept>>() {
      String policyId;

      @Override
      protected void checkParams() {
        policyId = findFirstValueAndRemove(spec.getCriteria(), "policyId", EQUAL);
        assertNotEmpty(policyId, "Parameter policyId is required");
        authPolicyQuery.checkAndFind(Long.valueOf(policyId), false, false);
      }

      @Override
      protected Page<Dept> process() {
        List<Long> deptIds = authPolicyOrgRepo.finOrgIdsByPolicyIdAndOrgType(
            Long.valueOf(policyId), AuthOrgType.DEPT.getValue());
        if (isNotEmpty(deptIds)) {
          spec.getCriteria().add(SearchCriteria.notIn("id", deptIds));
        }
        return deptRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<AuthPolicy> deptPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {
      String orgId;

      @Override
      protected void checkParams() {
        orgId = findFirstValue(spec.getCriteria(), "orgId", EQUAL);
        assertNotEmpty(orgId, "Parameter orgId is required");
        String orgType = findFirstValue(spec.getCriteria(), "orgType", EQUAL);
        assertNotEmpty(orgType, "Parameter orgType is required");
      }

      @Override
      protected Page<AuthPolicy> process() {
        closeMultiTenantCtrl();
        spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));
        boolean ignoreAuthOrg = getIgnoreAuthOrg(spec);
        return authOrgPolicyListRepo.find(spec.getCriteria(), pageable,
            AuthPolicy.class, ignoreAuthOrg ? AuthPolicyOrgConverter::objectArrToOrgAuthPolicy :
                AuthPolicyOrgConverter::objectArrToOrgAuthPolicyAndOrg, null);
      }
    }.execute();
  }

  /**
   * Query the policies that the current authorizer does not authorize to dept.
   */
  @Override
  public Page<AuthPolicy> deptUnauthPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {

      @Override
      protected Page<AuthPolicy> process() {
        GenericSpecification<AuthPolicy> specCopy = new GenericSpecification<>(spec.getCriteria());

        // Query the policies that have been authorized to users
        spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));
        Page<AuthPolicy> authorizedPolicies = deptPolicyList(spec,
            PageRequest.of(0, 5000, JpaSort.by(Order.asc("id"))));

        // Switch the authorization scope to all permissions of the current dept
        authPolicyUserQuery.checkSwitchUnAuthOrgCondition(specCopy);

        // Query the policies that not authorized to dept
        if (authorizedPolicies.hasContent()) {
          spec.getCriteria().add(SearchCriteria.notIn("id",
              authorizedPolicies.getContent().stream().map(AuthPolicy::getId)
                  .collect(Collectors.toSet())));
        }
        return authPolicyUserQuery.userAssociatedPolicyList(specCopy, pageable, true);
      }
    }.execute();
  }
}
