package cloud.xcan.angus.core.gm.application.query.authuser.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.TOKEN_NAME_EXISTED_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.api.obf.Str0;
import cloud.xcan.angus.api.pojo.Pair;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserTokenQuery;
import cloud.xcan.angus.core.gm.domain.authuser.AuthUserToken;
import cloud.xcan.angus.core.gm.domain.authuser.AuthUserTokenRepo;
import cloud.xcan.angus.spec.utils.crypto.AESUtils;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Biz
public class AuthUserTokenQueryImpl implements AuthUserTokenQuery {

  @Resource
  private AuthUserTokenRepo authUserTokenRepo;

  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  @Override
  public AuthUserToken value(Long id) {
    return new BizTemplate<AuthUserToken>() {
      AuthUserToken userToken;

      @Override
      protected void checkParams() {
        // Check the token exists and is the current user token
        userToken = checkAndFind(Collections.singleton(id)).get(0);
      }

      @Override
      protected AuthUserToken process() {
        userToken.setDecryptedValue(decryptValue(userToken.getValue()));
        return userToken;
      }
    }.execute();
  }

  @Override
  public List<AuthUserToken> list() {
    return new BizTemplate<List<AuthUserToken>>() {

      @Override
      protected List<AuthUserToken> process() {
        return authUserTokenRepo.findAllByCreatedBy(getUserId());
      }
    }.execute();
  }

  @Override
  public List<AuthUserToken> checkAndFind(Collection<Long> ids) {
    if (isEmpty(ids)) {
      return null;
    }
    List<AuthUserToken> userTokens = authUserTokenRepo.findByIdInAndCreatedBy(ids, getUserId());
    assertResourceNotFound(isNotEmpty(userTokens), ids.iterator().next(), "AuthUserToken");
    if (ids.size() != userTokens.size()) {
      for (AuthUserToken userToken : userTokens) {
        assertResourceNotFound(ids.contains(userToken.getId()), userToken.getId(), "AuthUserToken");
      }
    }
    return userTokens;
  }

  @Override
  public List<AuthUserToken> find0(Collection<Long> ids) {
    return authUserTokenRepo.findByIdInAndCreatedBy(ids, getUserId());
  }

  @Override
  public void checkNameNotExisted(AuthUserToken userToken) {
    assertResourceExisted(!authUserTokenRepo.existsByName(userToken.getName()),
        TOKEN_NAME_EXISTED_T, new Object[]{userToken.getName()});
  }

  @Override
  public void checkTokenQuota(Long userId, long incr) {
    if (incr > 0) {
      long num = authUserTokenRepo.countByTenantIdAndCreatedBy(getOptTenantId(), userId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.UserToken, null, num + incr);
    }
  }

  @Override
  public String encryptValue(String value) {
    return AESUtils.encrypt(
        Pair.of(new Str0(new long[]{0x4E212140E4A50C75L, 0xB611A4BC975C4BFBL, 0x4831FED56D6B4BF8L})
            .toString() /* => "XCanUserToken" */ + "." + getOptTenantId() + "." + new Str0(
            new long[]{0x7A8583F2887CDD91L, 0x35CE04A478ED551AL, 0xDA6B6B48ABF61AA9L})
            .toString() /* => "435E9A3AB63ED118" */, value));
  }

  @Override
  public String decryptValue(String value) {
    return AESUtils.decrypt(
        Pair.of(new Str0(new long[]{0x863731077291C1CBL, 0xE57910A474C48E17L, 0xE35535CC75D03B07L})
            .toString() /* => "XCanUserToken" */ + "." + getOptTenantId() + "." + new Str0(
            new long[]{0x7A8583F2887CDD91L, 0x35CE04A478ED551AL, 0xDA6B6B48ABF61AA9L})
            .toString() /* => "435E9A3AB63ED118" */, value));
  }

}
