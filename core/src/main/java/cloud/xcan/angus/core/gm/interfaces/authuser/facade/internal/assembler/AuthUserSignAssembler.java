package cloud.xcan.angus.core.gm.interfaces.authuser.facade.internal.assembler;


import static cloud.xcan.angus.spec.utils.ObjectUtils.isBlank;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignupDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.sign.AccountVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.sign.SignVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.utils.BeanFieldUtils;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import cloud.xcan.angus.spec.utils.JsonUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;


public class AuthUserSignAssembler {

  public static AuthUser signupToOauthUser(String deviceId, SignupDto dto) {
    AuthUser user = new AuthUser();
    user.setMobile(SignupType.MOBILE.equals(dto.getSignupType()) ? dto.getAccount() : null);
    user.setCountry(stringSafe(dto.getCountry()));
    user.setItc(stringSafe(dto.getItc()));
    user.setEmail(SignupType.EMAIL.equals(dto.getSignupType()) ? dto.getAccount() : null);
    user.setPassword(dto.getPassword());
    user.setSysAdmin(isBlank(dto.getInvitationCode()));
    user.setSignupType(dto.getSignupType().getValue());
    user.setVerificationCode(dto.getVerificationCode());
    user.setEmailBizKey(EmailBizKey.SIGNUP.getValue());
    user.setSmsBizKey(SmsBizKey.SIGNUP.getValue());
    user.setInvitationCode(dto.getInvitationCode());
    user.setSignupDeviceId(deviceId);
    return user;
  }

  public static SignVo signInToVo(Map<String, String> result) {
    return JsonUtils.fromJsonObject(result, SignVo.class);
  }

  public static AccountVo userToAccountVo(AuthUser user) {
    return new AccountVo().setUserId(Long.valueOf(user.getId()))
        .setTenantId(Long.valueOf(user.getTenantId()))
        .setLinkSecret(user.getLinkSecret())
        .setHasPassword(isNotBlank(user.getPassword()))
        .setTenantName(user.getTenantName());
  }

  public static Specification<AuthUser> getSpecification(Map<String, Object> params) {
    // De-duplication filters parameter
    Set<SearchCriteria> filters = new HashSet<>();
    // Merge filters parameter, The findDto parameter will override filters parameter
    params.forEach((key, value) -> {
      filters.add(new SearchCriteria(key, value, SearchOperation.EQUAL));
    });
    // Delete non-contracted query parameters
    List<String> propertyNames = BeanFieldUtils.getPropertyNames(AuthUser.class);
    filters.removeIf(criteria -> !propertyNames.contains(criteria.getKey()));
    return new GenericSpecification<>(filters);
  }
}
