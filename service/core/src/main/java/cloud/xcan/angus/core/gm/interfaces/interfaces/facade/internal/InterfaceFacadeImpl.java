package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal;

import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.InterfaceFacade;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceDeprecateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceSyncDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDeprecateVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceServiceVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceSyncVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceTagVo;
import cloud.xcan.angus.remote.PageResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Interface management facade implementation
 */
@Component
public class InterfaceFacadeImpl implements InterfaceFacade {

  @Resource
  private InterfaceQuery interfaceQuery;

  @Override
  public InterfaceSyncVo sync(InterfaceSyncDto dto) {
    // TODO: Implement sync from service
    InterfaceSyncVo vo = new InterfaceSyncVo();
    vo.setServiceName(dto.getServiceName());
    vo.setSyncTime(LocalDateTime.now());
    vo.setTotalInterfaces(0);
    vo.setNewInterfaces(0);
    vo.setUpdatedInterfaces(0);
    vo.setDeprecatedInterfaces(0);
    return vo;
  }

  @Override
  public InterfaceSyncVo syncAll() {
    // TODO: Implement sync all services
    InterfaceSyncVo vo = new InterfaceSyncVo();
    vo.setSyncTime(LocalDateTime.now());
    vo.setTotalInterfaces(0);
    vo.setNewInterfaces(0);
    vo.setUpdatedInterfaces(0);
    vo.setDeprecatedInterfaces(0);
    vo.setServices(new ArrayList<>());
    return vo;
  }

  @Override
  public InterfaceDeprecateVo deprecate(String id, InterfaceDeprecateDto dto) {
    // TODO: Implement deprecate interface
    InterfaceDeprecateVo vo = new InterfaceDeprecateVo();
    vo.setId(id);
    vo.setDeprecated(dto.getDeprecated());
    vo.setDeprecationNote(dto.getDeprecationNote());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  @Override
  public InterfaceDetailVo getDetail(String id) {
    // TODO: Implement get interface detail
    return new InterfaceDetailVo();
  }

  @Override
  public PageResult<InterfaceListVo> list(InterfaceFindDto dto) {
    // TODO: Implement list interfaces
    return PageResult.empty();
  }

  @Override
  public PageResult<InterfaceListVo> listByService(String serviceName, InterfaceFindDto dto) {
    // TODO: Implement list by service
    return PageResult.empty();
  }

  @Override
  public PageResult<InterfaceListVo> listByTag(String tag, InterfaceFindDto dto) {
    // TODO: Implement list by tag
    return PageResult.empty();
  }

  @Override
  public List<InterfaceServiceVo> getServices() {
    // TODO: Implement get services
    return new ArrayList<>();
  }

  @Override
  public List<InterfaceTagVo> getTags() {
    // TODO: Implement get tags
    return new ArrayList<>();
  }

  @Override
  public InterfaceCallStatsVo getCallStats(String id, InterfaceCallStatsDto dto) {
    // TODO: Implement get call stats
    return new InterfaceCallStatsVo();
  }
}
