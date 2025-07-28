package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.api.commonlink.AuthOrgType.USER;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotNull;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.SettingUserAssembler.toPreferenceTo;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler.signToDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserCurrentAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserCurrentAssembler.updateDtoToDomain;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getCountry;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.emptySafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.setting.user.preference.Preference;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.gm.app.vo.AppDetailVo;
import cloud.xcan.angus.api.gm.app.vo.OrgAppAuthVo;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.user.dto.UserCurrentUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.UserCurrentDetailVo;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserCurrentQuery;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppOrgAuthFacade;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthUserFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserCurrentFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.current.CheckSecretVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppTreeVo;
import cloud.xcan.angus.remote.InfoScope;
import jakarta.annotation.Resource;
import java.security.Principal;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class UserCurrentFacadeImpl implements UserCurrentFacade {

  @Resource
  private UserCurrentCmd userCurrentCmd;

  @Resource
  private UserCurrentQuery userCurrentQuery;

  @Resource
  private TenantFacade tenantFacade;

  @Resource
  private AppFacade appFacade;

  @Resource
  private AppOrgAuthFacade appOrgAuthFacade;

  @Resource
  private AuthUserFacade authUserFacade;

  @Resource
  private SettingUserCmd settingUserCmd;

  @Override
  public TenantDetailVo tenantDetail() {
    return tenantFacade.detail(getTenantId());
  }

  @Override
  public void currentUpdate(UserCurrentUpdateDto dto) {
    userCurrentCmd.updateCurrent(updateDtoToDomain(dto));
  }

  @Override
  public UserCurrentDetailVo currentDetail(InfoScope infoScope, String appCode,
      EditionType editionType) {
    boolean detailScope = nullSafe(infoScope, InfoScope.BASIC).equals(InfoScope.DETAIL);
    UserCurrentDetailVo vo = toDetailVo(userCurrentQuery.currentDetail(detailScope));

    if (detailScope) {
      assembleUserDetail(appCode, editionType, vo);
    }
    return vo;
  }

  @Override
  public void sendSms(CurrentSmsSendDto dto) {
    userCurrentQuery.sendSms(signToDomain(dto), emptySafe(dto.getCountry(), getCountry()));
  }

  @Override
  public CheckSecretVo checkSms(CurrentMobileCheckDto dto) {
    return new CheckSecretVo().setLinkSecret(
        userCurrentQuery.checkSms(dto.getBizKey(), dto.getMobile(), dto.getCountry(),
            dto.getVerificationCode()));
  }

  @Override
  public void updateMobile(CurrentMobileUpdateDto dto) {
    userCurrentCmd.mobileUpdate(dto.getMobile(), dto.getCountry(), dto.getItc(),
        dto.getVerificationCode(), dto.getLinkSecret(), dto.getBizKey());
  }

  @Override
  public void sendEmail(CurrentEmailSendDto dto) {
    userCurrentQuery.sendEmail(EmailAssembler.signToDomain(dto));
  }

  @Override
  public CheckSecretVo checkEmail(CurrentEmailCheckDto dto) {
    return new CheckSecretVo().setLinkSecret(userCurrentQuery.checkEmail(dto.getBizKey(),
        dto.getEmail(), dto.getVerificationCode()));
  }

  @Override
  public void updateEmail(CurrentEmailUpdateDto dto) {
    userCurrentCmd.updateEmail(dto.getEmail(), dto.getVerificationCode(),
        dto.getLinkSecret(), dto.getBizKey());
  }

  private void assembleUserDetail(String appCode, EditionType editionType, UserCurrentDetailVo vo) {
    assertNotNull(appCode, "appCode is required");
    assertNotNull(editionType, "editionType is required");

    long currentUserId = getUserId();

    Preference preferenceData = settingUserCmd.findAndInit(currentUserId).getPreference();
    vo.setPreference(toPreferenceTo(preferenceData));

    TenantDetailVo tenantDetail = tenantFacade.detail(getTenantId());
    vo.setTenant(tenantDetail);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Principal principal = (Principal) authentication.getPrincipal();
    vo.setPrincipal(principal);

    AppDetailVo accessApp = appFacade.detail(appCode, editionType);
    AuthAppTreeVo appTreeVo = authUserFacade.appFuncTree(currentUserId, accessApp.getCode(),
        false, true);
    accessApp.setAppFuncs(appTreeVo.getAppFuncs());
    vo.setAccessApp(accessApp);

    List<OrgAppAuthVo> authApps = appOrgAuthFacade.orgAuthApp(USER, currentUserId, false);
    vo.setAuthApps(authApps);
  }
}
