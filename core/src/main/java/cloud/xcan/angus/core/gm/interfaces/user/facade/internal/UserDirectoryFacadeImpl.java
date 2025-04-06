package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDirectoryAssembler.testDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDirectoryAssembler.toSyncVo;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDirectoryAssembler.toVo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.user.UserDirectoryCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserDirectoryQuery;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.model.DirectorySyncResult;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserDirectoryFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryAddDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryReorderDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory.UserDirectoryTestDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserDirectoryAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectoryDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectorySyncVo;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserDirectoryFacadeImpl implements UserDirectoryFacade {

  @Resource
  private UserDirectoryCmd userDirectoryCmd;

  @Resource
  private UserDirectoryQuery userDirectoryQuery;

  @Override
  public IdKey<Long, Object> add(UserDirectoryAddDto dto) {
    return userDirectoryCmd.add(UserDirectoryAssembler.addDtoToDomain(dto), false);
  }

  @Override
  public void replace(UserDirectoryReplaceDto dto) {
    userDirectoryCmd.replace(UserDirectoryAssembler.replaceDtoToDomain(dto));
  }

  @Override
  public void reorder(Set<UserDirectoryReorderDto> dto) {
    userDirectoryCmd.reorder(dto.stream().collect(Collectors.toMap(UserDirectoryReorderDto::getId,
        UserDirectoryReorderDto::getSequence)));
  }

  @Override
  public void enabled(Set<EnabledOrDisabledDto> dto) {
    List<UserDirectory> directories = dto.stream().map(UserDirectoryAssembler::enabledDtoToDomain)
        .collect(Collectors.toList());
    userDirectoryCmd.enabled(directories);
  }

  @Override
  public UserDirectorySyncVo sync(Long id) {
    return toSyncVo(userDirectoryCmd.sync(id, false));
  }

  @Override
  public Map<String, UserDirectorySyncVo> sync() {
    Map<String, DirectorySyncResult> results = userDirectoryCmd.sync();
    if (isEmpty(results)) {
      return Collections.emptyMap();
    }
    Map<String, UserDirectorySyncVo> vos = new HashMap<>();
    for (String x : results.keySet()) {
      vos.put(x, toSyncVo(results.get(x)));
    }
    return vos;
  }

  @Override
  public UserDirectorySyncVo test(UserDirectoryTestDto dto) {
    DirectorySyncResult result = userDirectoryCmd.test(testDtoToDomain(dto));
    return toSyncVo(result);
  }

  @Override
  public void delete(Long id, Boolean deleteSync) {
    userDirectoryCmd.delete(id, deleteSync);
  }

  @NameJoin
  @Override
  public UserDirectoryDetailVo detail(Long id) {
    return toVo(userDirectoryQuery.detail(id));
  }

  @NameJoin
  @Override
  public List<UserDirectoryDetailVo> list() {
    return userDirectoryQuery.list().stream().map(UserDirectoryAssembler::toVo)
        .collect(Collectors.toList());
  }
}
