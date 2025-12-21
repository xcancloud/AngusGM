package cloud.xcan.angus.core.gm.interfaces.interfaces.facade;

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
import java.util.List;

/**
 * Interface management facade
 */
public interface InterfaceFacade {

  /**
   * Sync interfaces for a service
   */
  InterfaceSyncVo sync(InterfaceSyncDto dto);

  /**
   * Sync all service interfaces
   */
  InterfaceSyncVo syncAll();

  /**
   * Deprecate an interface
   */
  InterfaceDeprecateVo deprecate(String id, InterfaceDeprecateDto dto);

  /**
   * Get interface detail
   */
  InterfaceDetailVo getDetail(String id);

  /**
   * Find interfaces with pagination
   */
  PageResult<InterfaceListVo> list(InterfaceFindDto dto);

  /**
   * List interfaces by service
   */
  PageResult<InterfaceListVo> listByService(String serviceName, InterfaceFindDto dto);

  /**
   * List interfaces by tag
   */
  PageResult<InterfaceListVo> listByTag(String tag, InterfaceFindDto dto);

  /**
   * Get all services with interface count
   */
  List<InterfaceServiceVo> getServices();

  /**
   * Get all tags with interface count
   */
  List<InterfaceTagVo> getTags();

  /**
   * Get interface call statistics
   */
  InterfaceCallStatsVo getCallStats(String id, InterfaceCallStatsDto dto);
}
