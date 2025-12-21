package cloud.xcan.angus.core.gm.interfaces.ldap;

import cloud.xcan.angus.core.gm.interfaces.ldap.facade.LdapFacade;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.*;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * LDAP集成REST接口
 * 
 * LDAP服务器配置、用户同步、认证集成
 */
@Tag(name = "LDAP", description = "LDAP集成 - LDAP服务器配置、用户同步、认证集成")
@RestController
@RequestMapping("/api/v1/ldap")
@RequiredArgsConstructor
public class LdapRest {
    
    private final LdapFacade ldapFacade;
    
    // ==================== LDAP配置 ====================
    
    @Operation(summary = "获取LDAP配置", description = "获取当前LDAP服务器配置")
    @GetMapping("/config")
    public ApiLocaleResult<LdapConfigVo> getConfig() {
        return ApiLocaleResult.success(ldapFacade.getConfig());
    }
    
    @Operation(summary = "更新LDAP配置", description = "更新LDAP服务器配置")
    @PutMapping("/config")
    public ApiLocaleResult<LdapConfigVo> updateConfig(@Valid @RequestBody LdapConfigUpdateDto dto) {
        return ApiLocaleResult.success(ldapFacade.updateConfig(dto));
    }
    
    // ==================== 连接测试 ====================
    
    @Operation(summary = "测试LDAP连接", description = "测试LDAP服务器连接是否正常")
    @PostMapping("/test-connection")
    public ApiLocaleResult<LdapConnectionTestVo> testConnection(@Valid @RequestBody LdapConnectionTestDto dto) {
        return ApiLocaleResult.success(ldapFacade.testConnection(dto));
    }
    
    @Operation(summary = "测试LDAP认证", description = "使用指定用户名密码测试LDAP认证")
    @PostMapping("/test-auth")
    public ApiLocaleResult<LdapAuthTestVo> testAuth(@Valid @RequestBody LdapAuthTestDto dto) {
        return ApiLocaleResult.success(ldapFacade.testAuth(dto));
    }
    
    // ==================== 用户同步 ====================
    
    @Operation(summary = "手动同步LDAP用户", description = "手动触发LDAP用户同步任务")
    @PostMapping("/sync-users")
    public ApiLocaleResult<LdapSyncResultVo> syncUsers() {
        return ApiLocaleResult.success(ldapFacade.syncUsers());
    }
    
    @Operation(summary = "获取同步历史记录", description = "分页获取LDAP用户同步历史记录")
    @GetMapping("/sync-history")
    public ApiLocaleResult<PageResult<LdapSyncHistoryVo>> listSyncHistory(@Valid LdapSyncHistoryFindDto dto) {
        return ApiLocaleResult.success(ldapFacade.listSyncHistory(dto));
    }
    
    @Operation(summary = "获取同步详情", description = "获取指定同步记录的详细信息")
    @GetMapping("/sync-history/{id}")
    public ApiLocaleResult<LdapSyncDetailVo> getSyncDetail(
            @Parameter(description = "同步记录ID") @PathVariable String id) {
        return ApiLocaleResult.success(ldapFacade.getSyncDetail(id));
    }
    
    // ==================== LDAP用户搜索 ====================
    
    @Operation(summary = "搜索LDAP用户", description = "在LDAP目录中搜索用户")
    @PostMapping("/search-users")
    public ApiLocaleResult<List<LdapUserVo>> searchUsers(@Valid @RequestBody LdapUserSearchDto dto) {
        return ApiLocaleResult.success(ldapFacade.searchUsers(dto));
    }
    
    // ==================== LDAP组管理 ====================
    
    @Operation(summary = "获取LDAP组列表", description = "获取LDAP目录中的组列表")
    @GetMapping("/groups")
    public ApiLocaleResult<List<LdapGroupVo>> listGroups() {
        return ApiLocaleResult.success(ldapFacade.listGroups());
    }
    
    @Operation(summary = "获取LDAP组成员", description = "获取指定LDAP组的成员列表")
    @GetMapping("/groups/{groupDN}/members")
    public ApiLocaleResult<LdapGroupMembersVo> getGroupMembers(
            @Parameter(description = "组DN（URL编码）") @PathVariable String groupDN) {
        return ApiLocaleResult.success(ldapFacade.getGroupMembers(groupDN));
    }
    
    // ==================== 字段映射 ====================
    
    @Operation(summary = "获取字段映射配置", description = "获取LDAP字段与系统字段的映射配置")
    @GetMapping("/field-mapping")
    public ApiLocaleResult<Map<String, String>> getFieldMapping() {
        return ApiLocaleResult.success(ldapFacade.getFieldMapping());
    }
    
    @Operation(summary = "更新字段映射配置", description = "更新LDAP字段与系统字段的映射配置")
    @PutMapping("/field-mapping")
    public ApiLocaleResult<Map<String, String>> updateFieldMapping(@RequestBody Map<String, String> mapping) {
        return ApiLocaleResult.success(ldapFacade.updateFieldMapping(mapping));
    }
}
