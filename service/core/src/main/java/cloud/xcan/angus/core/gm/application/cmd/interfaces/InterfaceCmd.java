package cloud.xcan.angus.core.gm.application.cmd.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.Interface;

/**
 * Interface command service interface
 */
public interface InterfaceCmd {

  /**
   * Deprecate an interface
   */
  Interface deprecate(Long id, Boolean deprecated, String deprecationNote);

  /**
   * Sync interfaces from service
   */
  Interface syncFromService(String serviceName);
}

