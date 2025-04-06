package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAddDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyInitDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicySearchDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface AuthPolicyFacade {

  List<IdKey<Long, Object>> add(List<AuthPolicyAddDto> dto);

  void update(List<AuthPolicyUpdateDto> dto);

  List<IdKey<Long, Object>> replace(List<AuthPolicyReplaceDto> dto);

  void enabled(List<EnabledOrDisabledDto> dto);

  void init(AuthPolicyInitDto dto);

  void delete(HashSet<Long> ids);

  AuthPolicyDetailVo detail(String idOrCode);

  PageResult<AuthPolicyVo> list(AuthPolicyFindDto dto);

  PageResult<AuthPolicyVo> search(AuthPolicySearchDto dto);

}
