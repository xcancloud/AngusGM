package cloud.xcan.angus.core.gm.application.cmd.authority.impl;

import static cloud.xcan.angus.core.gm.application.converter.ApiAuthorityConverter.toAppAuthority;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.api.ApiRepo;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.core.biz.Biz;
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

@Biz
public class ApiAuthorityCmdImpl extends CommCmd<ApiAuthority, Long> implements ApiAuthorityCmd {

  @Resource
  private ApiAuthorityRepo apiAuthorityRepo;

  @Resource
  private ApiRepo apiRepo;

  @Override
  public void replaceAppApiAuthority(App app) {
    if (isNotEmpty(app.getApiIds())) {
      deleteBySource(Set.of(app.getId()), ApiAuthoritySource.APP);
      saveAppApiAuthority(app);
    }
  }

  @Override
  public void saveAppApiAuthority(App app) {
    List<ApiAuthority> authorities = new ArrayList<>();
    if (isNotEmpty(app.getApiIds())) {
      List<Api> apisDb = apiRepo.findByIdIn(app.getApiIds());
      if (isNotEmpty(apisDb)) {
        for (Api api : apisDb) {
          authorities.add(toAppAuthority(app, api, uidGenerator.getUID()));
        }
      }
    }
    if (isNotEmpty(authorities)) {
      apiAuthorityRepo.batchInsert(authorities);
    }
  }

  @Override
  public void updateAppAuthorityStatus(List<App> apps, Set<Long> ids) {
    List<ApiAuthority> authorities = apiAuthorityRepo.findByAppIdIn(ids);
    if (isNotEmpty(authorities)) {
      for (ApiAuthority authority : authorities) {
        for (App app : apps) {
          if (app.getId().equals(authority.getAppId())) {
            authority.setAppEnabled(app.getEnabled());
          }
        }
      }
    }
    apiAuthorityRepo.saveAll(authorities);
  }

  @Override
  public void updateAuthorityServiceStatus(Set<Long> serviceIds, List<Service> services) {
    List<ApiAuthority> authorities = apiAuthorityRepo.findByServiceIdIn(serviceIds);
    if (isNotEmpty(authorities)) {
      for (ApiAuthority authority : authorities) {
        for (Service service : services) {
          if (service.getId().equals(authority.getServiceId())) {
            authority.setServiceEnabled(service.getEnabled());
          }
        }
      }
      apiAuthorityRepo.saveAll(authorities);
    }
  }

  @Override
  public void deleteBySource(Set<Long> sourceIds, ApiAuthoritySource source) {
    apiAuthorityRepo.deleteBySourceIdInAndSource(sourceIds, ApiAuthoritySource.APP.getValue());
  }

  @Override
  protected BaseRepository<ApiAuthority, Long> getRepository() {
    return apiAuthorityRepo;
  }
}
