package cloud.xcan.angus.core.gm.interfaces.security;

import cloud.xcan.angus.common.result.ApiLocaleResult;
import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.security.facade.SecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Security", description = "安全设置 - 密码策略、登录限制、IP白名单、安全审计")
@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class SecurityRest {

    private final SecurityFacade securityFacade;

    // ==================== 概览 ====================

    @Operation(summary = "获取安全设置概览", description = "获取安全设置总体概览")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/overview")
    public ApiLocaleResult<SecurityOverviewVo> getOverview() {
        return ApiLocaleResult.success(securityFacade.getOverview());
    }

    // ==================== 密码策略 ====================

    @Operation(summary = "获取密码策略配置", description = "获取密码策略配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/password-policy")
    public ApiLocaleResult<PasswordPolicyVo> getPasswordPolicy() {
        return ApiLocaleResult.success(securityFacade.getPasswordPolicy());
    }

    @Operation(summary = "更新密码策略配置", description = "更新密码策略配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @PutMapping("/password-policy")
    public ApiLocaleResult<PasswordPolicyVo> updatePasswordPolicy(@Valid @RequestBody PasswordPolicyUpdateDto dto) {
        return ApiLocaleResult.success(securityFacade.updatePasswordPolicy(dto));
    }

    // ==================== 双因素认证 ====================

    @Operation(summary = "获取双因素认证配置", description = "获取双因素认证配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/two-factor")
    public ApiLocaleResult<TwoFactorConfigVo> getTwoFactorConfig() {
        return ApiLocaleResult.success(securityFacade.getTwoFactorConfig());
    }

    @Operation(summary = "更新双因素认证配置", description = "更新双因素认证配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @PutMapping("/two-factor")
    public ApiLocaleResult<TwoFactorConfigVo> updateTwoFactorConfig(@Valid @RequestBody TwoFactorConfigUpdateDto dto) {
        return ApiLocaleResult.success(securityFacade.updateTwoFactorConfig(dto));
    }

    // ==================== IP白名单管理 ====================

    @Operation(summary = "获取IP白名单列表", description = "分页获取IP白名单列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/ip-whitelist")
    public ApiLocaleResult<PageResult<IpWhitelistVo>> listIpWhitelist(@ParameterObject IpWhitelistFindDto dto) {
        return ApiLocaleResult.success(securityFacade.listIpWhitelist(dto));
    }

    @Operation(summary = "添加IP白名单", description = "添加IP白名单条目")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "添加成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/ip-whitelist")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<IpWhitelistVo> addIpWhitelist(@Valid @RequestBody IpWhitelistCreateDto dto) {
        return ApiLocaleResult.success(securityFacade.addIpWhitelist(dto));
    }

    @Operation(summary = "更新IP白名单", description = "更新IP白名单条目")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "白名单不存在")
    })
    @PutMapping("/ip-whitelist/{id}")
    public ApiLocaleResult<IpWhitelistVo> updateIpWhitelist(
            @Parameter(description = "白名单ID") @PathVariable Long id,
            @Valid @RequestBody IpWhitelistUpdateDto dto) {
        return ApiLocaleResult.success(securityFacade.updateIpWhitelist(id, dto));
    }

    @Operation(summary = "删除IP白名单", description = "删除IP白名单条目")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "白名单不存在")
    })
    @DeleteMapping("/ip-whitelist/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIpWhitelist(@Parameter(description = "白名单ID") @PathVariable Long id) {
        securityFacade.deleteIpWhitelist(id);
    }

    // ==================== 会话管理 ====================

    @Operation(summary = "获取会话配置", description = "获取会话配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/session")
    public ApiLocaleResult<SessionConfigVo> getSessionConfig() {
        return ApiLocaleResult.success(securityFacade.getSessionConfig());
    }

    @Operation(summary = "更新会话配置", description = "更新会话配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @PutMapping("/session")
    public ApiLocaleResult<SessionConfigVo> updateSessionConfig(@Valid @RequestBody SessionConfigUpdateDto dto) {
        return ApiLocaleResult.success(securityFacade.updateSessionConfig(dto));
    }

    @Operation(summary = "获取活跃会话列表", description = "分页获取活跃会话列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/sessions/active")
    public ApiLocaleResult<PageResult<ActiveSessionVo>> listActiveSessions(@ParameterObject ActiveSessionFindDto dto) {
        return ApiLocaleResult.success(securityFacade.listActiveSessions(dto));
    }

    @Operation(summary = "强制注销会话", description = "强制注销指定会话")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "注销成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void terminateSession(@Parameter(description = "会话ID") @PathVariable String sessionId) {
        securityFacade.terminateSession(sessionId);
    }

    // ==================== 安全审计 ====================

    @Operation(summary = "获取安全事件列表", description = "分页获取安全事件列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/events")
    public ApiLocaleResult<PageResult<SecurityEventVo>> listSecurityEvents(@ParameterObject SecurityEventFindDto dto) {
        return ApiLocaleResult.success(securityFacade.listSecurityEvents(dto));
    }

    @Operation(summary = "标记安全事件为已处理", description = "标记安全事件为已处理")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "标记成功"),
            @ApiResponse(responseCode = "404", description = "事件不存在")
    })
    @PatchMapping("/events/{id}/handle")
    public ApiLocaleResult<SecurityEventHandleVo> handleSecurityEvent(
            @Parameter(description = "事件ID") @PathVariable Long id,
            @Valid @RequestBody SecurityEventHandleDto dto) {
        return ApiLocaleResult.success(securityFacade.handleSecurityEvent(id, dto));
    }

    @Operation(summary = "获取安全审计统计", description = "获取安全审计统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/audit-stats")
    public ApiLocaleResult<SecurityAuditStatsVo> getAuditStats(@ParameterObject SecurityAuditStatsFindDto dto) {
        return ApiLocaleResult.success(securityFacade.getAuditStats(dto));
    }
}
