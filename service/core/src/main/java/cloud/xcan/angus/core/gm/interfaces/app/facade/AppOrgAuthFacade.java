package cloud.xcan.angus.core.gm.interfaces.app.facade;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.gm.app.vo.OrgAppAuthVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgUserSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthOrgPolicyAuthDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthOrgPolicyAuthDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthUserVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthUserVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;

public interface AppOrgAuthFacade {

  PageResult<AppAuthTenantVo> appAuthTenant(Long appId, AuthAppOrgFindDto dto);

  PageResult<AppAuthUserVo> appAuthUser(Long appId, AuthAppOrgUserSearchDto dto);

  PageResult<AppAuthDeptVo> appAuthDept(Long appId, AuthAppOrgSearchDto dto);

  PageResult<AppAuthGroupVo> appAuthGroup(Long appId, AuthAppOrgSearchDto dto);

  PageResult<AppAuthPolicyVo> appAuthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      AuthAppOrgSearchDto dto);

  List<AppAuthPolicyVo> appAuthGlobal(Long appId);

  Boolean appAuthOrgCheck(Long appId, AuthOrgType orgType, Long orgId);

  List<OrgAppAuthVo> orgAuthApp(AuthOrgType orgType, Long orgId, Boolean joinPolicy);

  Boolean orgAuthAppCheck(AuthOrgType orgType, Long orgId, Long appId);

  PageResult<AppUnauthTenantVo> appUnauthTenant(Long appId, UnAuthFindDto dto);

  PageResult<AppUnauthUserVo> appUnauthUser(Long appId, UnAuthUserFindDto dto);

  PageResult<AppUnauthDeptVo> appUnauthDept(Long appId, UnAuthDeptFindDto dto);

  PageResult<AppUnauthGroupVo> appUnauthGroup(Long appId, UnAuthGroupFindDto dto);

  PageResult<AppAuthPolicyVo> appUnauthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      UnAuthFindDto dto);

  List<IdKey<Long, Object>> authUserPolicy(Long appId, AuthOrgPolicyAuthDto dto);

  List<IdKey<Long, Object>> authDeptPolicy(Long appId, AuthOrgPolicyAuthDto dto);

  List<IdKey<Long, Object>> authGroupPolicy(Long appId, AuthOrgPolicyAuthDto dto);

  void authUserDelete(Long appId, AuthOrgPolicyAuthDeleteDto dto);

  void authDeptDelete(Long appId, AuthOrgPolicyAuthDeleteDto dto);

  void authGroupDelete(Long appId, AuthOrgPolicyAuthDeleteDto dto);

}
