package cloud.xcan.angus.core.gm.application.query.system.impl;

import static cloud.xcan.angus.core.gm.domain.AuthMessage.TOKEN_NAME_EXISTED_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.checkTenantSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.api.commonlink.service.Service;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.api.obf.Str0;
import cloud.xcan.angus.api.pojo.Pair;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.application.query.system.SystemTokenQuery;
import cloud.xcan.angus.core.gm.domain.system.SystemToken;
import cloud.xcan.angus.core.gm.domain.system.SystemTokenRepo;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResource;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResourceRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.utils.crypto.AESUtils;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Biz
public class SystemTokenQueryImpl implements SystemTokenQuery {

  @Resource
  private SystemTokenRepo systemTokenRepo;

  @Resource
  private SystemTokenResourceRepo systemTokenResourceRepo;

  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  @Resource
  private ApiQuery apiQuery;

  @Resource
  private ServiceQuery serviceQuery;

  @Override
  public SystemToken auth(Long id) {
    return new BizTemplate<SystemToken>() {

      @Override
      protected SystemToken process() {
        SystemToken systemToken = systemTokenRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "AuthUserToken"));
        // Join resource api
        List<SystemTokenResource> resources = systemTokenResourceRepo.findBySystemTokenId(id);
        if (systemToken.isApiAuth()) {
          List<Long> resourceIds = resources.stream().map(x -> Long.parseLong(x.getAuthority()))
              .collect(Collectors.toList());
          List<Api> apis = apiQuery.findAllById(resourceIds);
          if (isNotEmpty(apis)) {
            Map<Long, Api> apiMap = apis.stream().collect(Collectors.toMap(Api::getId, x -> x));
            for (SystemTokenResource resource : resources) {
              resource.setApi(apiMap.get(Long.parseLong(resource.getAuthority())));
            }
          }

        }
        // Join service name
        Set<String> serviceCodes = resources.stream().map(SystemTokenResource::getServiceCode)
            .collect(Collectors.toSet());
        Map<String, Service> servicesMap = serviceQuery.checkAndFind(serviceCodes).stream()
            .collect(Collectors.toMap(Service::getCode, x -> x));
        for (SystemTokenResource resource : resources) {
          resource.setServiceName(servicesMap.get(resource.getServiceCode()).getName());
        }
        systemToken.setResources(resources);
        return systemToken;
      }
    }.execute();
  }

  @Override
  public SystemToken value(Long id) {
    return new BizTemplate<SystemToken>() {
      @Override
      protected void checkParams() {
        // Check must be a system admin
        checkTenantSysAdmin();
      }

      @Override
      protected SystemToken process() {
        SystemToken systemToken = systemTokenRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "AuthUserToken"));
        systemToken.setDecryptedValue(decryptValue(systemToken.getValue()));
        return systemToken;
      }
    }.execute();
  }

  @Override
  public List<SystemToken> list() {
    return new BizTemplate<List<SystemToken>>() {
      @Override
      protected List<SystemToken> process() {
        return systemTokenRepo.findAll();
      }
    }.execute();
  }

  @Override
  public SystemToken find0(Long id) {
    return systemTokenRepo.findById(id).orElse(null);
  }

  @Override
  public void checkNameNotExisted(SystemToken systemToken) {
    ProtocolAssert.assertResourceExisted(!systemTokenRepo.existsByName(systemToken.getName()),
        TOKEN_NAME_EXISTED_T, new Object[]{systemToken.getName()});
  }

  @Override
  public void checkTokenQuota(Long tenantId, long incr) {
    if (incr > 0) {
      long num = systemTokenRepo.countByTenantId(tenantId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.SystemToken, null, num + incr);
    }
  }

  @Override
  public String encryptValue(String value) {
    return AESUtils.encrypt(
        Pair.of(new Str0(new long[]{0x231236753DF64C33L, 0x3E304342F297835DL, 0x84FB5E43B36A0C85L})
            .toString() /* => "XCanSysToken" */ + "." + getOptTenantId() + "." + new Str0(
            new long[]{0x7A8583F2887CDD91L, 0x35CE04A478ED551AL, 0xDA6B6B48ABF61AA9L})
            .toString() /* => "435E9A3AB63ED118" */, value));
  }

  @Override
  public String decryptValue(String value) {
    return AESUtils.decrypt(
        Pair.of(new Str0(new long[]{0x14FCDD296C72B045L, 0xAF8B12E2ADE53624L, 0xCB8EBDC6C911124EL})
            .toString() /* => "XCanSysToken" */ + "." + getOptTenantId() + "." + new Str0(
            new long[]{0x7A8583F2887CDD91L, 0x35CE04A478ED551AL, 0xDA6B6B48ABF61AA9L})
            .toString() /* => "435E9A3AB63ED118" */, value));
  }
}
