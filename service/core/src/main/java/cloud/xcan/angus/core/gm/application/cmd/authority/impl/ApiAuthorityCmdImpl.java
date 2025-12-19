package cloud.xcan.angus.core.gm.application.cmd.authority.impl;

import static cloud.xcan.angus.core.gm.application.converter.ApiAuthorityConverter.toAppAuthority;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authority.ApiAuthorityCmd;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthority;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthorityRepo;
import cloud.xcan.angus.core.gm.domain.authority.ApiAuthoritySource;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of API authority command operations for managing API permissions.
 *
 * <p>This class provides comprehensive functionality for API authority management including:</p>
 * <ul>
 *   <li>Managing application API authorities and permissions</li>
 *   <li>Handling service and application status updates</li>
 *   <li>Batch operations for authority management</li>
 *   <li>Source-based authority deletion</li>
 * </ul>
 *
 * <p>The implementation ensures proper API permission management across
 * applications and services with consistent authority tracking.</p>
 */
@org.springframework.stereotype.Service
public class ApiAuthorityCmdImpl extends CommCmd<ApiAuthority, Long> implements ApiAuthorityCmd {

  @Resource
  private ApiAuthorityRepo apiAuthorityRepo;

  @Resource
  private ApiRepo apiRepo;

  /**
   * Replaces application API authorities by deleting existing and creating new ones.
   *
   * <p>This method performs comprehensive authority replacement including:</p>
   * <ul>
   *   <li>Deleting existing authorities for the application</li>
   *   <li>Creating new authorities based on current API configuration</li>
   *   <li>Maintaining authority source tracking</li>
   * </ul>
   *
   * @param app Application entity with API configuration
   */
  @Override
  public void replaceAppApiAuthority(App app) {
    if (isNotEmpty(app.getApiIds())) {
      // Delete existing authorities for the application
      deleteBySource(Set.of(app.getId()), ApiAuthoritySource.APP);
      // Save new application API authorities
      saveAppApiAuthority(app);
    }
  }

  /**
   * Saves application API authorities based on API configuration.
   *
   * <p>This method creates authority records for each API associated with the application,
   * ensuring proper permission mapping between applications and APIs.</p>
   *
   * @param app Application entity with API identifiers
   */
  @Override
  public void saveAppApiAuthority(App app) {
    List<ApiAuthority> authorities = new ArrayList<>();
    if (isNotEmpty(app.getApiIds())) {
      // Retrieve APIs by identifiers
      List<Api> apisDb = apiRepo.findByIdIn(app.getApiIds());
      if (isNotEmpty(apisDb)) {
        // Create authority records for each API
        for (Api api : apisDb) {
          authorities.add(toAppAuthority(app, api, uidGenerator.getUID()));
        }
      }
    }
    // Batch insert authorities for performance
    if (isNotEmpty(authorities)) {
      apiAuthorityRepo.batchInsert(authorities);
    }
  }

  /**
   * Updates application authority status based on application enabled state.
   *
   * <p>This method synchronizes authority status with application status,
   * ensuring that disabled applications have corresponding disabled authorities.</p>
   *
   * @param apps List of applications with updated status
   * @param ids  Set of application identifiers to update
   */
  @Override
  public void updateAppAuthorityStatus(List<App> apps, Set<Long> ids) {
    // Find authorities for specified applications
    List<ApiAuthority> authorities = apiAuthorityRepo.findByAppIdIn(ids);
    if (isNotEmpty(authorities)) {
      // Update authority status based on application status
      for (ApiAuthority authority : authorities) {
        for (App app : apps) {
          if (app.getId().equals(authority.getAppId())) {
            authority.setAppEnabled(app.getEnabled());
          }
        }
      }
    }
    // Save updated authorities
    apiAuthorityRepo.saveAll(authorities);
  }

  /**
   * Updates authority service status based on service enabled state.
   *
   * <p>This method synchronizes authority status with service status,
   * ensuring that disabled services have corresponding disabled authorities.</p>
   *
   * @param serviceIds Set of service identifiers to update
   * @param services   List of services with updated status
   */
  @Override
  public void updateAuthorityServiceStatus(Set<Long> serviceIds, List<Service> services) {
    // Find authorities for specified services
    List<ApiAuthority> authorities = apiAuthorityRepo.findByServiceIdIn(serviceIds);
    if (isNotEmpty(authorities)) {
      // Update authority status based on service status
      for (ApiAuthority authority : authorities) {
        for (Service service : services) {
          if (service.getId().equals(authority.getServiceId())) {
            authority.setServiceEnabled(service.getEnabled());
          }
        }
      }
      // Save updated authorities
      apiAuthorityRepo.saveAll(authorities);
    }
  }

  /**
   * Deletes authorities by source type and identifiers.
   *
   * <p>This method removes authority records based on their source type
   * and associated identifiers.</p>
   *
   * @param sourceIds Set of source identifiers to delete
   * @param source    Source type for authority deletion
   */
  @Override
  public void deleteBySource(Set<Long> sourceIds, ApiAuthoritySource source) {
    apiAuthorityRepo.deleteBySourceIdInAndSource(sourceIds, ApiAuthoritySource.APP.getValue());
  }

  @Override
  protected BaseRepository<ApiAuthority, Long> getRepository() {
    return apiAuthorityRepo;
  }
}
