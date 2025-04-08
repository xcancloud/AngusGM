package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuotaRepo;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.exception.QuotaException;
import cloud.xcan.angus.spec.unit.DataSize;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Biz
public class SettingTenantQuotaManagerImpl implements SettingTenantQuotaManager {

  @Autowired(required = false)
  private SettingTenantQuotaRepo commonTenantQuotaRepo;

  @Autowired(required = false)
  private SettingManager settingManager;

  /**
   * @param resIds The resource ID is null when adding a resource, Use name instead!
   */
  @Override
  public void checkTenantQuota(QuotaResource quotaResource, Set<?> resIds, Long num) {
    if (num == 0) {
      return;
    }
    SettingTenantQuota quotas = findTenantQuota(
        getOptTenantId()/*It may be called through /doorapi and job*/, quotaResource);
    if (quotaResource == QuotaResource.User && quotas.getQuota() < num) {
      throw QuotaException.of(USER_OVER_LIMIT_CODE, USER_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.UserDept && quotas.getQuota() < num) {
      throw QuotaException.of(USER_DEPT_OVER_LIMIT_CODE, USER_DEPT_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.UserGroup && quotas.getQuota() < num) {
      throw QuotaException.of(USER_GROUP_OVER_LIMIT_CODE, USER_GROUP_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.Dept && quotas.getQuota() < num) {
      throw QuotaException.of(DEPT_OVER_LIMIT_CODE, DEPT_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.DeptLevel && quotas.getQuota() < num) {
      throw QuotaException.of(DEPT_LEVEL_OVER_LIMIT_CODE, DEPT_LEVEL_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.DeptUser && quotas.getQuota() < num) {
      throw QuotaException.of(DEPT_USER_OVER_LIMIT_CODE, DEPT_USER_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.Group && quotas.getQuota() < num) {
      throw QuotaException.of(GROUP_OVER_LIMIT_CODE, GROUP_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.GroupUser && quotas.getQuota() < num) {
      throw QuotaException.of(GROUP_USER_OVER_LIMIT_CODE, GROUP_USER_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.OrgTag && quotas.getQuota() < num) {
      throw QuotaException.of(ORG_TAG_OVER_LIMIT_CODE, ORG_TAG_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.OrgTargetTag && quotas.getQuota() < num) {
      throw QuotaException.of(ORG_TARGET_TAG_OVER_LIMIT_CODE, ORG_TARGET_TAG_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.PolicyCustom && quotas.getQuota() < num) {
      throw QuotaException.of(POLICY_CUSTOM_OVER_LIMIT_CODE, POLICY_CUSTOM_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.SystemToken && quotas.getQuota() < num) {
      throw QuotaException.of(SYSTEM_TOKEN_OVER_LIMIT_CODE, SYSTEM_TOKEN_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.UserToken && quotas.getQuota() < num) {
      throw QuotaException.of(USER_TOKEN_OVER_LIMIT_CODE, USER_TOKEN_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.FileStore && quotas.getQuota() < num) {
      throw QuotaException.of(STORAGE_SIZE_OVER_LIMIT_CODE, STORAGE_SIZE_OVER_LIMIT_T,
          new Object[]{DataSize.ofBytes(quotas.getQuota()).toHumanString()});
    }
    if (quotaResource == QuotaResource.DataSpace && quotas.getQuota() < num) {
      throw QuotaException.of(STORAGE_DATA_SPACE_OVER_LIMIT_CODE, STORAGE_DATA_SPACE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterProject && quotas.getQuota() < num) {
      throw QuotaException.of(AT_PROJECT_OVER_LIMIT_CODE, AT_PROJECT_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterServices && quotas.getQuota() < num) {
      throw QuotaException.of(AT_SERVICES_OVER_LIMIT_CODE, AT_SERVICES_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterApis && quotas.getQuota() < num) {
      throw QuotaException.of(AT_APIS_OVER_LIMIT_CODE, AT_APIS_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterServicesApis && quotas.getQuota() < num) {
      throw QuotaException.of(AT_SERVICES_APIS_OVER_LIMIT_CODE, AT_SERVICES_APIS_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterVariable && quotas.getQuota() < num) {
      throw QuotaException.of(AT_VARIABLE_OVER_LIMIT_CODE, AT_VARIABLE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterTargetVariable && quotas.getQuota() < num) {
      throw QuotaException.of(AT_TARGET_VARIABLE_OVER_LIMIT_CODE, AT_TARGET_VARIABLE_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterDataset && quotas.getQuota() < num) {
      throw QuotaException.of(AT_DATASET_OVER_LIMIT_CODE, AT_DATASET_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterTargetDataset && quotas.getQuota() < num) {
      throw QuotaException.of(AT_TARGET_DATASET_OVER_LIMIT_CODE, AT_TARGET_DATASET_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterScenario && quotas.getQuota() < num) {
      throw QuotaException.of(AT_SCE_OVER_LIMIT_CODE, AT_SCE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterScenarioApis && quotas.getQuota() < num) {
      throw QuotaException.of(AT_SCE_API_OVER_LIMIT_CODE, AT_SCE_API_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterMockService && quotas.getQuota() < num) {
      throw QuotaException.of(AT_MOCK_SERVICE_OVER_LIMIT_CODE, AT_MOCK_SERVICE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterMockServiceApis && quotas.getQuota() < num) {
      throw QuotaException.of(AT_MOCK_SERVICE_APIS_OVER_LIMIT_CODE,
          AT_MOCK_SERVICE_APIS_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterMockApisResponse && quotas.getQuota() < num) {
      throw QuotaException.of(AT_MOCK_APIS_RESPONSE_OVER_LIMIT_CODE,
          AT_MOCK_APIS_RESPONSE_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterMockIterations && quotas.getQuota() < num) {
      throw QuotaException.of(AT_MOCK_ITERATIONS_OVER_LIMIT_CODE, AT_MOCK_ITERATIONS_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterMockDatasource && quotas.getQuota() < num) {
      throw QuotaException.of(AT_MOCK_DATASOURCE_OVER_LIMIT_CODE, AT_MOCK_DATASOURCE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterNode && quotas.getQuota() < num) {
      throw QuotaException.of(AT_NODE_OVER_LIMIT_CODE, AT_NODE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterConcurrency && quotas.getQuota() < num) {
      throw QuotaException.of(AT_CONCURRENCY_OVER_LIMIT_CODE, AT_CONCURRENCY_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterConcurrentTask && quotas.getQuota() < num) {
      throw QuotaException.of(AT_CONCURRENCY_TASK_OVER_LIMIT_CODE, AT_CONCURRENCY_TASK_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterScript && quotas.getQuota() < num) {
      throw QuotaException.of(AT_SCRIPT_OVER_LIMIT_CODE, AT_SCRIPT_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterExecution && quotas.getQuota() < num) {
      throw QuotaException.of(AT_EXECUTION_OVER_LIMIT_CODE, AT_EXECUTION_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterExecutionDebug && quotas.getQuota() < num) {
      throw QuotaException.of(AT_EXECUTION_DEBUG_OVER_LIMIT_CODE, AT_EXECUTION_DEBUG_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterReport && quotas.getQuota() < num) {
      throw QuotaException.of(AT_REPORT_OVER_LIMIT_CODE, AT_REPORT_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterTask && quotas.getQuota() < num) {
      throw QuotaException.of(AT_TASK_OVER_LIMIT_CODE, AT_TASK_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterSprint && quotas.getQuota() < num) {
      throw QuotaException.of(AT_SPRINT_OVER_LIMIT_CODE, AT_SPRINT_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterSprintTask && quotas.getQuota() < num) {
      throw QuotaException.of(AT_SPRINT_TASK_OVER_LIMIT_CODE, AT_SPRINT_TASK_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterFuncPlan && quotas.getQuota() < num) {
      throw QuotaException.of(AT_FUNC_PLAN_OVER_LIMIT_CODE, AT_FUNC_PLAN_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterFuncCase && quotas.getQuota() < num) {
      throw QuotaException.of(AT_FUNC_CASE_OVER_LIMIT_CODE, AT_FUNC_CASE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterFuncPlanCase && quotas.getQuota() < num) {
      throw QuotaException.of(AT_FUNC_PLAN_CASE_OVER_LIMIT_CODE, AT_FUNC_PLAN_CASE_OVER_LIMIT_T,
          new Object[]{getSafeObjectMsg(resIds), quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterTag && quotas.getQuota() < num) {
      throw QuotaException.of(AT_FUNC_TAG_OVER_LIMIT_CODE, AT_FUNC_TAG_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
    if (quotaResource == QuotaResource.AngusTesterModule && quotas.getQuota() < num) {
      throw QuotaException.of(AT_FUNC_MODULE_OVER_LIMIT_CODE, AT_FUNC_MODULE_OVER_LIMIT_T,
          new Object[]{quotas.getQuota()});
    }
  }

  /**
   * Read global default when tenant does not set quota.
   */
  @Override
  public SettingTenantQuota findTenantQuota(Long tenantId, QuotaResource name) {
    Optional<SettingTenantQuota> tenantQuota = commonTenantQuotaRepo
        .findByTenantIdAndName(tenantId, name.getValue());
    if (tenantQuota.isPresent()) {
      return tenantQuota.get();
    }
    Quota quotaData = settingManager.setting(SettingKey.QUOTA).findQuotaByName(name.getValue());
    assertResourceNotFound(quotaData, tenantId, name.getValue());
    return quotaData.toTenantQuota(tenantId);
  }

  @Override
  public List<SettingTenantQuota> findTenantQuotas(Long tenantId) {
    return commonTenantQuotaRepo.findByTenantId(tenantId);
  }

  private static String getSafeObjectMsg(Set<?> objectIds) {
    if (isEmpty(objectIds)) {
      return "";
    }
    // Fix: lambda size =1, All elements are null when add resource id is null
    Object res = objectIds.stream().findFirst().orElse(null);
    return Objects.nonNull(res) ? res.toString() : "";
  }
}
