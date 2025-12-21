package cloud.xcan.angus.core.gm.interfaces.security;

import cloud.xcan.angus.core.gm.interfaces.security.facade.SecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.ActiveSessionFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.PasswordPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityAuditStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityEventFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityEventHandleDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SessionConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.TwoFactorConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.ActiveSessionVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.IpWhitelistVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityAuditStatsVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityEventHandleVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityEventVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SessionConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.TwoFactorConfigVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Security", description = "安全设置 - 密码策略、登录限制、IP白名单、安全审计")
@Validated
@RestController
@RequestMapping("/api/v1/security")
public class SecurityRest {

  @Resource
  private SecurityFacade securityFacade;

  @Operation(operationId = "updatePasswordPolicy", summary = "更新密码策略配置", description = "更新密码策略配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/password-policy")
  public ApiLocaleResult<PasswordPolicyVo> updatePasswordPolicy(
      @Valid @RequestBody PasswordPolicyUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updatePasswordPolicy(dto));
  }

  @Operation(operationId = "updateTwoFactorConfig", summary = "更新双因素认证配置", description = "更新双因素认证配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/two-factor")
  public ApiLocaleResult<TwoFactorConfigVo> updateTwoFactorConfig(
      @Valid @RequestBody TwoFactorConfigUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updateTwoFactorConfig(dto));
  }

  @Operation(operationId = "updateIpWhitelist", summary = "更新IP白名单", description = "更新IP白名单条目")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "白名单不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/ip-whitelist/{id}")
  public ApiLocaleResult<IpWhitelistVo> updateIpWhitelist(
      @Parameter(description = "白名单ID") @PathVariable Long id,
      @Valid @RequestBody IpWhitelistUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updateIpWhitelist(id, dto));
  }

  @Operation(operationId = "updateSessionConfig", summary = "更新会话配置", description = "更新会话配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/session")
  public ApiLocaleResult<SessionConfigVo> updateSessionConfig(
      @Valid @RequestBody SessionConfigUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updateSessionConfig(dto));
  }

  @Operation(operationId = "handleSecurityEvent", summary = "标记安全事件为已处理", description = "标记安全事件为已处理")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标记成功"),
      @ApiResponse(responseCode = "404", description = "事件不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/events/{id}/handle")
  public ApiLocaleResult<SecurityEventHandleVo> handleSecurityEvent(
      @Parameter(description = "事件ID") @PathVariable Long id,
      @Valid @RequestBody SecurityEventHandleDto dto) {
    return ApiLocaleResult.success(securityFacade.handleSecurityEvent(id, dto));
  }

  @Operation(operationId = "addIpWhitelist", summary = "添加IP白名单", description = "添加IP白名单条目")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "添加成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/ip-whitelist")
  public ApiLocaleResult<IpWhitelistVo> addIpWhitelist(
      @Valid @RequestBody IpWhitelistCreateDto dto) {
    return ApiLocaleResult.success(securityFacade.addIpWhitelist(dto));
  }

  @Operation(operationId = "deleteIpWhitelist", summary = "删除IP白名单", description = "删除IP白名单条目")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "404", description = "白名单不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/ip-whitelist/{id}")
  public void deleteIpWhitelist(
      @Parameter(description = "白名单ID") @PathVariable Long id) {
    securityFacade.deleteIpWhitelist(id);
  }

  @Operation(operationId = "terminateSession", summary = "强制注销会话", description = "强制注销指定会话")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "注销成功"),
      @ApiResponse(responseCode = "404", description = "会话不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/sessions/{sessionId}")
  public void terminateSession(
      @Parameter(description = "会话ID") @PathVariable String sessionId) {
    securityFacade.terminateSession(sessionId);
  }

  @Operation(operationId = "getSecurityOverview", summary = "获取安全设置概览", description = "获取安全设置总体概览")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/overview")
  public ApiLocaleResult<SecurityOverviewVo> getOverview() {
    return ApiLocaleResult.success(securityFacade.getOverview());
  }

  @Operation(operationId = "getPasswordPolicy", summary = "获取密码策略配置", description = "获取密码策略配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/password-policy")
  public ApiLocaleResult<PasswordPolicyVo> getPasswordPolicy() {
    return ApiLocaleResult.success(securityFacade.getPasswordPolicy());
  }

  @Operation(operationId = "getTwoFactorConfig", summary = "获取双因素认证配置", description = "获取双因素认证配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/two-factor")
  public ApiLocaleResult<TwoFactorConfigVo> getTwoFactorConfig() {
    return ApiLocaleResult.success(securityFacade.getTwoFactorConfig());
  }

  @Operation(operationId = "getIpWhitelist", summary = "获取IP白名单列表", description = "分页获取IP白名单列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/ip-whitelist")
  public ApiLocaleResult<PageResult<IpWhitelistVo>> listIpWhitelist(
      @ParameterObject IpWhitelistFindDto dto) {
    return ApiLocaleResult.success(securityFacade.listIpWhitelist(dto));
  }

  @Operation(operationId = "getSessionConfig", summary = "获取会话配置", description = "获取会话配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/session")
  public ApiLocaleResult<SessionConfigVo> getSessionConfig() {
    return ApiLocaleResult.success(securityFacade.getSessionConfig());
  }

  @Operation(operationId = "getActiveSessions", summary = "获取活跃会话列表", description = "分页获取活跃会话列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/sessions/active")
  public ApiLocaleResult<PageResult<ActiveSessionVo>> listActiveSessions(
      @ParameterObject ActiveSessionFindDto dto) {
    return ApiLocaleResult.success(securityFacade.listActiveSessions(dto));
  }

  @Operation(operationId = "getSecurityEvents", summary = "获取安全事件列表", description = "分页获取安全事件列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/events")
  public ApiLocaleResult<PageResult<SecurityEventVo>> listSecurityEvents(
      @ParameterObject SecurityEventFindDto dto) {
    return ApiLocaleResult.success(securityFacade.listSecurityEvents(dto));
  }

  @Operation(operationId = "getSecurityAuditStats", summary = "获取安全审计统计", description = "获取安全审计统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/audit-stats")
  public ApiLocaleResult<SecurityAuditStatsVo> getAuditStats(
      @ParameterObject SecurityAuditStatsFindDto dto) {
    return ApiLocaleResult.success(securityFacade.getAuditStats(dto));
  }
}
