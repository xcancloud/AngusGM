package cloud.xcan.angus.core.gm.application.query.to.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.TO_POLICY_IS_DISABLED_T;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.PARAM_VALUE_DUPLICATE_T;
import static cloud.xcan.angus.remote.message.http.ResourceExisted.M.RESOURCE_ALREADY_EXISTS_T2;
import static cloud.xcan.angus.spec.utils.ObjectUtils.duplicateByKey;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.api.commonlink.to.TORoleRepo;
import cloud.xcan.angus.api.commonlink.to.TORoleUser;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.to.TORoleQuery;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


@Biz
public class TORoleQueryImpl implements TORoleQuery {

  @Resource
  private TORoleRepo toRoleRepo;

  @Resource
  private TORoleUserRepo toRoleUserRepo;

  @Resource
  private UserRepo userRepo;

  @Override
  public TORole detail(String idOrCode) {
    return new BizTemplate<TORole>() {

      @Override
      protected TORole process() {
        TORole toPolicyDb = null;
        if (NumberUtils.isDigits(idOrCode)) {
          toPolicyDb = find0(Long.parseLong(idOrCode));
        }
        if (Objects.isNull(toPolicyDb)) {
          toPolicyDb = toRoleRepo.findByCode(idOrCode).orElse(null);
        }

        assertResourceNotFound(toPolicyDb, idOrCode, "TORole");

        // Join policy users
        setPolicyUser(toPolicyDb);
        return toPolicyDb;
      }
    }.execute();
  }

  @Override
  public Page<TORole> list(Specification<TORole> spec, PageRequest pageable) {
    return new BizTemplate<Page<TORole>>() {

      @Override
      protected Page<TORole> process() {
        return toRoleRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public TORole find0(Long policyId) {
    return toRoleRepo.findById(policyId).orElse(null);
  }

  @Override
  public List<TORole> findAllById(Set<Long> ids) {
    return toRoleRepo.findAllByIdIn(ids);
  }

  @Override
  public TORole checkAndFind(Long policyId, boolean checkEnabled) {
    return checkAndFind(List.of(policyId), checkEnabled).get(0);
  }

  @Override
  public List<TORole> checkAndFind(Collection<Long> policyIds, boolean checkEnabled) {
    if (isEmpty(policyIds)) {
      return null;
    }
    List<TORole> policies = toRoleRepo.findAllByIdIn(policyIds);
    assertResourceNotFound(isNotEmpty(policies), policyIds.iterator().next(), "TORole");
    if (policyIds.size() != policies.size()) {
      for (TORole policy : policies) {
        assertResourceNotFound(policyIds.contains(policy.getId()), policy.getId(), "TORole");
      }
    }
    if (checkEnabled) {
      for (TORole policy : policies) {
        assertTrue(policy.getEnabled(), TO_POLICY_IS_DISABLED_T, new Object[]{policy.getName()});
      }
    }
    return policies;
  }

  @Override
  public void checkDuplicateInParam(List<TORole> apps) {
    if (isEmpty(apps)) {
      return;
    }
    List<TORole> duplicateCodePolicy = apps.stream().filter(x -> nonNull(x.getCode()))
        .filter(duplicateByKey(TORole::getCode)).collect(Collectors.toList());
    assertTrue(isEmpty(duplicateCodePolicy), PARAM_VALUE_DUPLICATE_T, new Object[]{
        "code", isEmpty(duplicateCodePolicy) ? null : duplicateCodePolicy.get(0).getCode()});
    List<TORole> duplicateNamePolicy = apps.stream().filter(x -> nonNull(x.getName()))
        .filter(duplicateByKey(TORole::getName)).collect(Collectors.toList());
    assertTrue(isEmpty(duplicateNamePolicy), PARAM_VALUE_DUPLICATE_T, new Object[]{
        "name", isEmpty(duplicateNamePolicy) ? null : duplicateNamePolicy.get(0).getName()});
  }

  @Override
  public void checkUniqueCodeAndName(List<TORole> policies) {
    if (isEmpty(policies)) {
      return;
    }
    for (TORole policy : policies) {
      if (nonNull(policy.getId())) {
        // Update or replace
        assertResourceExisted(isEmpty(policy.getCode()) ||
            toRoleRepo.existsByCodeAndIdNot(policy.getCode(),
                policy.getId()), RESOURCE_ALREADY_EXISTS_T2, policy.getCode());
        assertResourceExisted(isEmpty(policy.getCode()) ||
            !toRoleRepo.existsByNameAndIdNot(policy.getName(),
                policy.getId()), RESOURCE_ALREADY_EXISTS_T2, policy.getName());
      } else {
        // Insert
        assertResourceExisted(!toRoleRepo.existsByCode(policy.getCode()),
            RESOURCE_ALREADY_EXISTS_T2, policy.getCode());
        assertResourceExisted(!toRoleRepo.existsByName(policy.getName()),
            RESOURCE_ALREADY_EXISTS_T2, policy.getName());
      }
    }
  }

  private void setPolicyUser(TORole toPolicyDb) {
    List<TORoleUser> tpu = toRoleUserRepo.findAllByToRoleIdIn(
        Collections.singletonList(toPolicyDb.getId()));
    List<User> topUsers = userRepo
        .findAllById(tpu.stream().map(TORoleUser::getUserId).collect(Collectors.toSet()));
    toPolicyDb.setUsers(topUsers);
  }
}
