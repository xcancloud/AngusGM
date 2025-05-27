package cloud.xcan.angus.core.gm.application.query.app.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.APP_IS_DISABLED_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isMultiTenantCtrl;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.remote.message.http.ResourceExisted.M.RESOURCE_ALREADY_EXISTS_T2;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.math.NumberUtils.isDigits;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.app.AppListRepo;
import cloud.xcan.angus.core.gm.domain.app.AppRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Biz
public class AppQueryImpl implements AppQuery {

  @Resource
  private AppRepo appRepo;

  @Resource
  private AppListRepo appListRepo;

  @Resource
  private ApiQuery apiQuery;

  @Resource
  private WebTagQuery webTagQuery;

  @Override
  public App detail(Long id) {
    return new BizTemplate<App>(false) {

      @Override
      protected App process() {
        App appDb = checkAndFind(String.valueOf(id));

        // Join api
        if (isNotEmpty(appDb.getApiIds())) {
          appDb.setApis(apiQuery.findAllById(appDb.getApiIds()));
        }

        // Join tag
        appDb.setTags(webTagQuery.findByTargetId(appDb.getId()));
        return appDb;
      }
    }.execute();
  }

  @Override
  public App detail(String code, EditionType editionType) {
    return new BizTemplate<App>(false) {

      @Override
      protected App process() {
        App appDb = checkAndFind(code, editionType);

        // Join api
        if (isNotEmpty(appDb.getApiIds())) {
          appDb.setApis(apiQuery.findAllById(appDb.getApiIds()));
        }

        // Join tag
        appDb.setTags(webTagQuery.findByTargetId(appDb.getId()));
        return appDb;
      }
    }.execute();
  }

  @Override
  public Page<App> find(GenericSpecification<App> spec, Pageable pageable) {
    return new BizTemplate<Page<App>>(false) {

      @Override
      protected Page<App> process() {
        return appListRepo.find(spec.getCriteria(), pageable, App.class, null);
      }
    }.execute();
  }

  @Override
  public App findLatestByCode(String code) {
    return appRepo.findLatestByCode(code);
  }

  @Override
  public App findLatestByCode(String code, EditionType editionType) {
    return appRepo.findLatestByCodeAndEditionType(code, editionType.getValue());
  }

  @Override
  public List<App> findByIdIn(Collection<Long> ids) {
    return appRepo.findAllById(ids);
  }

  @Override
  public Map<Long, App> findMapById(Collection<Long> ids) {
    return appRepo.findAllById(ids).stream().collect(Collectors.toMap(App::getId, x -> x));
  }

  @Override
  public App checkAndFind(String idOrCode) {
    App appDb = null;
    if (isDigits(idOrCode)) {
      appDb = appRepo.findById(Long.parseLong(idOrCode)).orElse(null);
    }
    if (isNull(appDb)) {
      appDb = findLatestByCode(idOrCode);
      if (isNull(appDb)) {
        throw ResourceNotFound.of(idOrCode, "App");
      }
    }
    return appDb;
  }

  @Override
  public App checkAndFind(String code, EditionType editionType) {
    App appDb = findLatestByCode(code, editionType);
    if (isNull(appDb)) {
      throw ResourceNotFound.of(editionType.getValue() + ":" + code, "App");
    }
    return appDb;
  }

  @Override
  public App checkAndFind(Long appId, boolean checkEnabled) {
    return checkAndFind(List.of(appId), checkEnabled).get(0);
  }

  @Override
  public App checkAndFind(EditionType editionType, String appCode, String version,
      boolean checkEnabled) {
    App app = appRepo.findByEditionTypeAndCodeAndVersion(editionType, appCode, version)
        .orElseThrow(() -> ResourceNotFound
            .of(editionType.getValue() + " " + appCode + "-" + version, "App"));
    if (checkEnabled) {
      assertTrue(app.getEnabled(), APP_IS_DISABLED_T, new Object[]{app.getName()});
    }
    return app;
  }

  @Override
  public List<App> checkAndFind(Collection<Long> appIds, boolean checkEnabled) {
    if (isEmpty(appIds)) {
      return null;
    }
    List<App> apps = appRepo.findAllByIdIn(appIds);
    assertResourceNotFound(isNotEmpty(apps), appIds.iterator().next(), "App");
    if (appIds.size() != apps.size()) {
      for (App app : apps) {
        assertResourceNotFound(appIds.contains(app.getId()), app.getId(), "App");
      }
    }
    if (checkEnabled) {
      for (App app : apps) {
        assertTrue(app.getEnabled(), APP_IS_DISABLED_T, new Object[]{app.getName()});
      }
    }
    return apps;
  }

  @Override
  public List<App> checkAndFindTenantApp(Collection<Long> appIds, boolean checkEnabled) {
    if (isEmpty(appIds)) {
      return null;
    }

    // Turn off multi tenant control
    boolean isMultiTenantCtrl = isMultiTenantCtrl();
    setMultiTenantCtrl(false);
    List<App> appsDb = checkAndFind(appIds, checkEnabled);
    setMultiTenantCtrl(isMultiTenantCtrl);

    for (App app : appsDb) {
      assertTrue(app.isTenantApp(), String.format("%s is not tenant application", app.getId()));
    }
    return appsDb;
  }

  @Override
  public void checkUniqueCodeAndVersion(App app) {
    // These three parameters are required
    assertTrue(isNotEmpty(app.getCode())
            && isNotEmpty(app.getEditionType()) && isNotEmpty(app.getVersion()),
        "Parameter code, editionType, version value is required");

    if (nonNull(app.getId())) {
      // Update or replace
      assertResourceExisted(!appRepo.existsByCodeAndEditionTypeAndVersionAndIdNot(app.getCode(),
              app.getEditionType(), app.getVersion(), app.getId()), RESOURCE_ALREADY_EXISTS_T2,
          new Object[]{app.getEditionType().getValue() + " " + app.getCode()
              + "-" + app.getVersion()});
    } else {
      // Insert
      assertResourceExisted(!appRepo.existsByCodeAndEditionTypeAndVersion(app.getCode(),
              app.getEditionType(), app.getVersion()), RESOURCE_ALREADY_EXISTS_T2,
          new Object[]{app.getEditionType().getValue() + " " + app.getCode()
              + "-" + app.getVersion()});
    }
  }

}
