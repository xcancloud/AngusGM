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
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyGroupQuery;
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

/**
 * <p>
 * Implementation of authentication policy group query operations.
 * </p>
 * <p>
 * Manages group-policy relationship queries, validation, and authorization management. Provides
 * comprehensive group-policy querying with authorization support.
 * </p>
 * <p>
 * Supports policy-group queries, group-policy queries, authorization management, and unauthorized
 * policy queries for comprehensive group-policy administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class AuthPolicyGroupQueryImpl implements AuthPolicyGroupQuery {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private AuthOrgPolicyListRepo authOrgPolicyListRepo;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private GroupRepo groupRepo;
  @Resource
  private AuthPolicyUserQuery authPolicyUserQuery;

  /**
   * <p>
   * Retrieves groups associated with specific policy.
   * </p>
   * <p>
   * Queries groups that are authorized by the specified policy. Validates policy existence and
   * handles multi-tenant control.
   * </p>
   */
  @Override
  public Page<Group> policyGroupList(GenericSpecification<Group> spec, PageRequest pageable) {
    return new BizTemplate<Page<Group>>() {
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
      protected Page<Group> process() {
        List<Long> groupIds = authPolicyOrgRepo.finOrgIdsByPolicyIdAndOrgType(
            Long.valueOf(policyId), AuthOrgType.GROUP.getValue());
        if (isEmpty(groupIds)) {
          return Page.empty();
        }
        spec.getCriteria().add(SearchCriteria.in("id", groupIds));
        return groupRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves groups not associated with specific policy.
   * </p>
   * <p>
   * Queries groups that are not authorized by the specified policy. Validates policy existence for
   * proper filtering.
   * </p>
   */
  @Override
  public Page<Group> policyUnauthGroupList(GenericSpecification<Group> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<Group>>() {
      String policyId;

      @Override
      protected void checkParams() {
        policyId = findFirstValueAndRemove(spec.getCriteria(), "policyId", EQUAL);
        assertNotEmpty(policyId, "Parameter policyId is required");
        authPolicyQuery.checkAndFind(Long.valueOf(policyId), false, false);
      }

      @Override
      protected Page<Group> process() {
        List<Long> groupIds = authPolicyOrgRepo.finOrgIdsByPolicyIdAndOrgType(
            Long.valueOf(policyId), AuthOrgType.GROUP.getValue());
        if (isNotEmpty(groupIds)) {
          spec.getCriteria().add(SearchCriteria.notIn("id", groupIds));
        }
        return groupRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves policies associated with specific group.
   * </p>
   * <p>
   * Queries policies that are authorized to the specified group. Validates organization parameters
   * and handles authorization filtering.
   * </p>
   */
  @Override
  public Page<AuthPolicy> groupPolicyList(GenericSpecification<AuthPolicy> spec,
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
        boolean ignoreAuthOrg = getIgnoreAuthOrg(spec);
        spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));
        return authOrgPolicyListRepo.find(spec.getCriteria(), pageable,
            AuthPolicy.class, ignoreAuthOrg ? AuthPolicyOrgConverter::objectArrToOrgAuthPolicy :
                AuthPolicyOrgConverter::objectArrToOrgAuthPolicyAndOrg, null);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves policies not authorized to group.
   * </p>
   * <p>
   * Queries policies that the current authorizer does not authorize to group. Compares authorized
   * policies with all available policies to find unauthorized ones.
   * </p>
   */
  @Override
  public Page<AuthPolicy> groupUnauthPolicyList(GenericSpecification<AuthPolicy> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<AuthPolicy>>() {

      @Override
      protected Page<AuthPolicy> process() {
        GenericSpecification<AuthPolicy> specCopy = new GenericSpecification<>(spec.getCriteria());

        // Query the policies that have been authorized to group
        spec.getCriteria().add(SearchCriteria.equal("clientId", getClientId()));
        Page<AuthPolicy> authorizedPolicies = groupPolicyList(spec,
            PageRequest.of(0, 5000, JpaSort.by(Order.asc("id"))));

        // Switch the authorization scope to all permissions of the current group
        authPolicyUserQuery.checkSwitchUnAuthOrgCondition(specCopy);

        // Query the policies that not authorized to group
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
