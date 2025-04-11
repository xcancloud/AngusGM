package cloud.xcan.angus.api.commonlink.setting.quota;


import cloud.xcan.angus.spec.locale.EnumMessage;

// Max 2000
public enum QuotaResource implements EnumMessage<String> {
  /*UC*/User, UserDept, UserGroup, Dept, DeptLevel, DeptUser, Group, GroupUser, OrgTag, OrgTargetTag,
  /*AAS*/PolicyCustom, SystemToken, UserToken,
  /*Storage*/FileStore/* Alias::CloudSpace */, DataSpace,
  /*AngusTester*/AngusTesterProject, AngusTesterServices, AngusTesterApis, AngusTesterServicesApis,
  AngusTesterVariable, AngusTesterTargetVariable, AngusTesterDataset, AngusTesterTargetDataset, AngusTesterScenario, AngusTesterScenarioApis,
  AngusTesterMockService, AngusTesterMockServiceApis, AngusTesterMockApisResponse, AngusTesterMockIterations, AngusTesterMockDatasource,
  AngusTesterNode, AngusTesterConcurrency, AngusTesterConcurrentTask,
  AngusTesterScript, AngusTesterExecution, AngusTesterExecutionDebug, AngusTesterReport,
  AngusTesterSprint, AngusTesterTask, AngusTesterSprintTask,
  AngusTesterFuncPlan, AngusTesterFuncCase, AngusTesterFuncPlanCase,
  AngusTesterTag, AngusTesterModule
  /*Cpu, Memory, SystemDisk, DataDisk, Bandwidth*/;

  @Override
  public String getValue() {
    return this.name();
  }
}
