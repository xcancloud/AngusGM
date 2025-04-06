package cloud.xcan.angus.api.manager;

public interface QuotaMessage {

  /*<******************Authorize(BCO001 ~ BCO0029)******************>*/
  String TENANT_QUOTA_NOT_INIT_CODE = "BCO001";
  String TENANT_QUOTA_NOT_INIT_T = "xcm.common.quota.not.init.t";
  String PRODUCT_LIC_OBTAIN_ERROR_CODE = "BCO002";
  String PRODUCT_LIC_OBTAIN_ERROR = "xcm.common.software.product.lic.obtain.error";

  /*<******************Quota(BCO050 ~ BCO099)******************>*/
  // UC
  String USER_OVER_LIMIT_CODE = "BCO111";
  String USER_OVER_LIMIT_T = "xcm.common.user.over.limit.t";
  String USER_DEPT_OVER_LIMIT_CODE = "BCO112";
  String USER_DEPT_OVER_LIMIT_T = "xcm.common.user.dept.over.limit.t";
  String USER_GROUP_OVER_LIMIT_CODE = "BCO113";
  String USER_GROUP_OVER_LIMIT_T = "xcm.common.user.group.over.limit.t";
  String DEPT_OVER_LIMIT_CODE = "BCO114";
  String DEPT_OVER_LIMIT_T = "xcm.common.dept.over.limit.t";
  String DEPT_LEVEL_OVER_LIMIT_CODE = "BCO115";
  String DEPT_LEVEL_OVER_LIMIT_T = "xcm.common.dept.Level.over.limit.t";
  String DEPT_USER_OVER_LIMIT_CODE = "BCO116";
  String DEPT_USER_OVER_LIMIT_T = "xcm.common.dept.user.over.limit.t";
  String GROUP_OVER_LIMIT_CODE = "BCO117";
  String GROUP_OVER_LIMIT_T = "xcm.common.group.over.limit.t";
  String GROUP_USER_OVER_LIMIT_CODE = "BCO118";
  String GROUP_USER_OVER_LIMIT_T = "xcm.common.group.user.over.limit.t";
  String ORG_TAG_OVER_LIMIT_CODE = "BCO119";
  String ORG_TAG_OVER_LIMIT_T = "xcm.common.org.tag.over.limit.t";
  String ORG_TARGET_TAG_OVER_LIMIT_CODE = "BCO110";
  String ORG_TARGET_TAG_OVER_LIMIT_T = "xcm.common.org.target.tag.over.limit.t";

  // AAS
  String POLICY_CUSTOM_OVER_LIMIT_CODE = "BCO120";
  String POLICY_CUSTOM_OVER_LIMIT_T = "xcm.common.policy.custom.over.limit.t";
  String SYSTEM_TOKEN_OVER_LIMIT_CODE = "BCO121";
  String SYSTEM_TOKEN_OVER_LIMIT_T = "xcm.common.system.token.over.limit.t";
  String USER_TOKEN_OVER_LIMIT_CODE = "BCO122";
  String USER_TOKEN_OVER_LIMIT_T = "xcm.common.user.token.over.limit.t";

  // Storage
  String STORAGE_SIZE_OVER_LIMIT_CODE = "BCO130";
  String STORAGE_SIZE_OVER_LIMIT_T = "xcm.common.storage.size.over.limit.t";
  String STORAGE_DATA_SPACE_OVER_LIMIT_CODE = "BCO131";
  String STORAGE_DATA_SPACE_OVER_LIMIT_T = "xcm.common.storage.data.space.over.limit.t";

  // AngusTester
  String AT_PROJECT_OVER_LIMIT_CODE = "BCO140";
  String AT_PROJECT_OVER_LIMIT_T = "xcm.common.at.project.over.limit.t";
  String AT_SERVICES_OVER_LIMIT_CODE = "BCO141";
  String AT_SERVICES_OVER_LIMIT_T = "xcm.common.at.services.over.limit.t";
  String AT_APIS_OVER_LIMIT_CODE = "BCO142";
  String AT_APIS_OVER_LIMIT_T = "xcm.common.at.apis.over.limit.t";
  String AT_SERVICES_APIS_OVER_LIMIT_CODE = "BCO143";
  String AT_SERVICES_APIS_OVER_LIMIT_T = "xcm.common.at.services.apis.over.limit.t";
  String AT_VARIABLE_OVER_LIMIT_CODE = "BCO144";
  String AT_VARIABLE_OVER_LIMIT_T = "xcm.common.at.variable.over.limit.t";
  String AT_TARGET_VARIABLE_OVER_LIMIT_CODE = "BCO145";
  String AT_TARGET_VARIABLE_OVER_LIMIT_T = "xcm.common.at.target.variable.over.limit.t";
  String AT_DATASET_OVER_LIMIT_CODE = "BCO146";
  String AT_DATASET_OVER_LIMIT_T = "xcm.common.at.dataset.over.limit.t";
  String AT_TARGET_DATASET_OVER_LIMIT_CODE = "BCO147";
  String AT_TARGET_DATASET_OVER_LIMIT_T = "xcm.common.at.target.dataset.over.limit.t";

  String AT_SCE_OVER_LIMIT_CODE = "BCO152";
  String AT_SCE_OVER_LIMIT_T = "xcm.common.at.sce.over.limit.t";
  String AT_SCE_API_OVER_LIMIT_CODE = "BCO153";
  String AT_SCE_API_OVER_LIMIT_T = "xcm.common.at.sce.apis.over.limit.t";

  String AT_MOCK_SERVICE_OVER_LIMIT_CODE = "BCO160";
  String AT_MOCK_SERVICE_OVER_LIMIT_T = "xcm.common.at.mock.service.over.limit.t";
  String AT_MOCK_SERVICE_APIS_OVER_LIMIT_CODE = "BCO161";
  String AT_MOCK_SERVICE_APIS_OVER_LIMIT_T = "xcm.common.at.mock.service.apis.over.limit.t";
  String AT_MOCK_APIS_RESPONSE_OVER_LIMIT_CODE = "BCO162";
  String AT_MOCK_APIS_RESPONSE_OVER_LIMIT_T = "xcm.common.at.mock.apis.response.over.limit.t";
  String AT_MOCK_ITERATIONS_OVER_LIMIT_CODE = "BCO163";
  String AT_MOCK_ITERATIONS_OVER_LIMIT_T = "xcm.common.at.mock.iterations.over.limit.t";
  String AT_MOCK_DATASOURCE_OVER_LIMIT_CODE = "BCO164";
  String AT_MOCK_DATASOURCE_OVER_LIMIT_T = "xcm.common.at.mock.datasource.over.limit.t";

  String AT_NODE_OVER_LIMIT_CODE = "BCO170";
  String AT_NODE_OVER_LIMIT_T = "xcm.common.at.node.over.limit.t";
  String AT_CONCURRENCY_OVER_LIMIT_CODE = "BCO171";
  String AT_CONCURRENCY_OVER_LIMIT_T = "xcm.common.at.concurrency.over.limit.t";
  String AT_CONCURRENCY_TASK_OVER_LIMIT_CODE = "BCO172";
  String AT_CONCURRENCY_TASK_OVER_LIMIT_T = "xcm.common.at.concurrency.task.over.limit.t";

  String AT_SCRIPT_OVER_LIMIT_CODE = "BCO175";
  String AT_SCRIPT_OVER_LIMIT_T = "xcm.common.at.script.over.limit.t";
  String AT_EXECUTION_OVER_LIMIT_CODE = "BCO176";
  String AT_EXECUTION_OVER_LIMIT_T = "xcm.common.at.execution.over.limit.t";
  String AT_EXECUTION_DEBUG_OVER_LIMIT_CODE = "BCO177";
  String AT_EXECUTION_DEBUG_OVER_LIMIT_T = "xcm.common.at.execution.debug.over.limit.t";
  String AT_REPORT_OVER_LIMIT_CODE = "BCO178";
  String AT_REPORT_OVER_LIMIT_T = "xcm.common.at.report.over.limit.t";

  String AT_TASK_OVER_LIMIT_CODE = "BCO182";
  String AT_TASK_OVER_LIMIT_T = "xcm.common.at.task.over.limit.t";
  String AT_SPRINT_OVER_LIMIT_CODE = "BCO183";
  String AT_SPRINT_OVER_LIMIT_T = "xcm.common.at.sprint.over.limit.t";
  String AT_SPRINT_TASK_OVER_LIMIT_CODE = "BCO184";
  String AT_SPRINT_TASK_OVER_LIMIT_T = "xcm.common.at.sprint.task.over.limit.t";

  String AT_FUNC_PLAN_OVER_LIMIT_CODE = "BCO192";
  String AT_FUNC_PLAN_OVER_LIMIT_T = "xcm.common.at.func.plan.over.limit.t";
  String AT_FUNC_CASE_OVER_LIMIT_CODE = "BCO193";
  String AT_FUNC_CASE_OVER_LIMIT_T = "xcm.common.at.func.case.over.limit.t";
  String AT_FUNC_PLAN_CASE_OVER_LIMIT_CODE = "BCO194";
  String AT_FUNC_PLAN_CASE_OVER_LIMIT_T = "xcm.common.at.func.plan.case.over.limit.t";
  String AT_FUNC_TAG_OVER_LIMIT_CODE = "BCO195";
  String AT_FUNC_TAG_OVER_LIMIT_T = "xcm.common.at.func.tag.over.limit.t";
  String AT_FUNC_MODULE_OVER_LIMIT_CODE = "BCO196";
  String AT_FUNC_MODULE_OVER_LIMIT_T = "xcm.common.at.func.module.over.limit.t";

}
