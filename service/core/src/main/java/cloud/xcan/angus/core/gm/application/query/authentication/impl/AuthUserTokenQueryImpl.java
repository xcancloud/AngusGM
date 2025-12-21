package cloud.xcan.angus.core.gm.application.query.authentication.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.TOKEN_NAME_EXISTED_T;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;
import cloud.xcan.angus.api.obf.Str0;
import cloud.xcan.angus.api.pojo.Pair;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthUserTokenQuery;
import cloud.xcan.angus.core.gm.domain.authentication.AuthUserToken;
import cloud.xcan.angus.core.gm.domain.authentication.AuthUserTokenRepo;
import cloud.xcan.angus.spec.utils.crypto.AESUtils;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Implementation of authentication user token query operations.
 * </p>
 * <p>
 * Manages user token retrieval, validation, and encryption/decryption.
 * Provides comprehensive token querying with security encryption support.
 * </p>
 * <p>
 * Supports token detail retrieval, quota validation, encryption/decryption,
 * and token name validation for secure user token management.
 * </p>
 */
@Biz
public class AuthUserTokenQueryImpl implements AuthUserTokenQuery {

  @Resource
  private AuthUserTokenRepo authUserTokenRepo;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  /**
   * <p>
   * Retrieves detailed user token information with decrypted value.
   * </p>
   * <p>
   * Fetches complete token record and decrypts the token value.
   * Verifies token belongs to current user for security.
   * </p>
   */
  @Override
  public AuthUserToken value(Long id) {
    return new BizTemplate<AuthUserToken>() {
      AuthUserToken userToken;

      @Override
      protected void checkParams() {
        // Verify token exists and belongs to current user
        userToken = checkAndFind(Collections.singleton(id)).get(0);
      }

      @Override
      protected AuthUserToken process() {
        userToken.setDecryptedValue(decryptValue(userToken.getValue()));
        return userToken;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves all user tokens for current user.
   * </p>
   * <p>
   * Returns list of all tokens created by the current user.
   * </p>
   */
  @Override
  public List<AuthUserToken> list(String appCode) {
    return new BizTemplate<List<AuthUserToken>>() {

      @Override
      protected List<AuthUserToken> process() {
        return isNull(appCode) ? authUserTokenRepo.findAllByCreatedBy(getUserId())
            : authUserTokenRepo.findAllByCreatedByAndGenerateAppCode(getUserId(), appCode);
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves multiple user tokens by IDs.
   * </p>
   * <p>
   * Verifies all tokens exist and belong to current user.
   * Validates complete collection match and throws appropriate exceptions.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves user tokens by IDs without validation.
   * </p>
   * <p>
   * Returns tokens that belong to current user without validation checks.
   * Used for non-critical token lookups.
   * </p>
   */
  @Override
  public List<AuthUserToken> find0(Collection<Long> ids) {
    return authUserTokenRepo.findByIdInAndCreatedBy(ids, getUserId());
  }

  /**
   * <p>
   * Validates token name does not already exist.
   * </p>
   * <p>
   * Checks for duplicate token names to ensure uniqueness.
   * Throws ResourceExisted exception if token name already exists.
   * </p>
   */
  @Override
  public void checkNameNotExisted(AuthUserToken userToken) {
    assertResourceExisted(!authUserTokenRepo.existsByName(userToken.getName()),
        TOKEN_NAME_EXISTED_T, new Object[]{userToken.getName()});
  }

  /**
   * <p>
   * Validates user token quota for tenant.
   * </p>
   * <p>
   * Checks if adding tokens would exceed tenant quota limits.
   * Throws appropriate exception if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkTokenQuota(Long userId, long incr) {
    if (incr > 0) {
      long num = authUserTokenRepo.countByTenantIdAndCreatedBy(getOptTenantId(), userId);
      settingTenantQuotaManager.checkTenantQuota(QuotaResource.UserToken, null, num + incr);
    }
  }

  /**
   * <p>
   * Encrypts token value for secure storage.
   * </p>
   * <p>
   * Uses AES encryption with tenant-specific key for secure token storage.
   * </p>
   */
  @Override
  public String encryptValue(String value) {
    return AESUtils.encrypt(
        Pair.of(new Str0(new long[]{0x4E212140E4A50C75L, 0xB611A4BC975C4BFBL, 0x4831FED56D6B4BF8L})
            .toString() /* => "XCanUserToken" */ + "." + getOptTenantId() + "." + new Str0(
            new long[]{0x7A8583F2887CDD91L, 0x35CE04A478ED551AL, 0xDA6B6B48ABF61AA9L})
            .toString() /* => "435E9A3AB63ED118" */, value));
  }

  /**
   * <p>
   * Decrypts token value for secure retrieval.
   * </p>
   * <p>
   * Uses AES decryption with tenant-specific key for secure token retrieval.
   * </p>
   */
  @Override
  public String decryptValue(String value) {
    return AESUtils.decrypt(
        Pair.of(new Str0(new long[]{0x863731077291C1CBL, 0xE57910A474C48E17L, 0xE35535CC75D03B07L})
            .toString() /* => "XCanUserToken" */ + "." + getOptTenantId() + "." + new Str0(
            new long[]{0x7A8583F2887CDD91L, 0x35CE04A478ED551AL, 0xDA6B6B48ABF61AA9L})
            .toString() /* => "435E9A3AB63ED118" */, value));
  }

}
