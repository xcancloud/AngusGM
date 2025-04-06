package cloud.xcan.angus.core.gm.application.query.app;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface AppFuncQuery {

  AppFunc detail(Long id);

  List<AppFunc> list(GenericSpecification<AppFunc> spec);

  List<AppFunc> findAndAllParent(Collection<AppFunc> funcs);

  List<AppFunc> checkAndFind(Long appId, Collection<Long> funcIds, boolean checkEnabled);

  List<AppFunc> checkAndFindTenantAppFunc(Long appId, Collection<Long> funcIds,
      boolean checkEnabled);

  void checkAddCodeExist(Long appId, List<AppFunc> funcs);

  void checkUpdateCodeExist(Long appId, List<AppFunc> funcs);

  void checkRepeatedCodeInParams(List<AppFunc> funcs);

  AppFunc checkAndFind(Long id, boolean checkEnabled);

  List<AppFunc> checkAndFind(Collection<Long> ids, boolean checkEnabled);

  List<AppFunc> findAllByAppId(Long appId, Boolean onlyEnabled);

  List<AppFunc> findFuncAndSub(Long appId, Collection<Long> funcIds);

  List<AppFunc> findSub(Long appId, Collection<Long> funcIds);

  Set<Long> findFuncAndSubIds(Long appId, Collection<Long> funcIds);

  List<Long> findSubIds(Long appId, Collection<Long> funcIds);

  List<AppFunc> findValidByPolicyIds(Collection<Long> authFuncIds);

  List<AppFunc> findByPolicyIds(Collection<Long> policyIds);

  void setTargetTags(List<AppFunc> appFunctions);

  void setApis(List<AppFunc> appFunctions);

  void setTags(List<AppFunc> appFunctions);

  void setApis(App app);
}
