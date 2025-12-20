package cloud.xcan.angus.core.gm.interfaces.ldap;

import cloud.xcan.angus.commons.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.LdapFacade;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "LDAP管理", description = "LDAP集成管理接口")
@RestController
@RequestMapping("/api/v1/ldap")
@RequiredArgsConstructor
public class LdapRest {
    
    private final LdapFacade ldapFacade;
    
    @Operation(summary = "创建LDAP配置")
    @PostMapping
    public ApiLocaleResult<LdapDetailVo> create(@RequestBody LdapCreateDto dto) {
        return ApiLocaleResult.success(ldapFacade.create(dto));
    }
    
    @Operation(summary = "更新LDAP配置")
    @PatchMapping
    public ApiLocaleResult<LdapDetailVo> update(@RequestBody LdapUpdateDto dto) {
        return ApiLocaleResult.success(ldapFacade.update(dto));
    }
    
    @Operation(summary = "删除LDAP配置")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "LDAP ID") @PathVariable Long id) {
        ldapFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "获取LDAP详情")
    @GetMapping("/{id}")
    public ApiLocaleResult<LdapDetailVo> findById(@Parameter(description = "LDAP ID") @PathVariable Long id) {
        return ApiLocaleResult.success(ldapFacade.findById(id));
    }
    
    @Operation(summary = "查询LDAP列表")
    @GetMapping
    public ApiLocaleResult<List<LdapListVo>> findAll(LdapFindDto dto) {
        return ApiLocaleResult.success(ldapFacade.findAll(dto));
    }
    
    @Operation(summary = "获取LDAP统计")
    @GetMapping("/stats")
    public ApiLocaleResult<LdapStatsVo> getStats() {
        return ApiLocaleResult.success(ldapFacade.getStats());
    }
}
