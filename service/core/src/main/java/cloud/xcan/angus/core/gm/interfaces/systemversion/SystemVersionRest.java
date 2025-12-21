package cloud.xcan.angus.core.gm.interfaces.systemversion;

import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.SystemVersionFacade;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.*;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统版本REST接口
 * 
 * 系统版本信息、更新管理、变更日志
 */
@Tag(name = "SystemVersion", description = "系统版本 - 系统版本信息、更新管理、变更日志")
@RestController
@RequestMapping("/api/v1/system-version")
@RequiredArgsConstructor
public class SystemVersionRest {
    
    private final SystemVersionFacade systemVersionFacade;
    
    // ==================== 版本信息 ====================
    
    @Operation(summary = "获取当前系统版本信息", description = "获取当前运行的系统版本信息")
    @GetMapping("/current")
    public ApiLocaleResult<CurrentVersionVo> getCurrentVersion() {
        return ApiLocaleResult.success(systemVersionFacade.getCurrentVersion());
    }
    
    @Operation(summary = "获取版本历史列表", description = "分页获取版本历史列表")
    @GetMapping("/history")
    public ApiLocaleResult<PageResult<VersionHistoryVo>> listVersionHistory(@Valid VersionHistoryFindDto dto) {
        return ApiLocaleResult.success(systemVersionFacade.listVersionHistory(dto));
    }
    
    @Operation(summary = "获取版本详情", description = "获取指定版本的详细信息")
    @GetMapping("/history/{id}")
    public ApiLocaleResult<VersionDetailVo> getVersionDetail(
            @Parameter(description = "版本ID") @PathVariable String id) {
        return ApiLocaleResult.success(systemVersionFacade.getVersionDetail(id));
    }
    
    // ==================== 变更日志 ====================
    
    @Operation(summary = "获取变更日志", description = "获取版本变更日志")
    @GetMapping("/changelog")
    public ApiLocaleResult<PageResult<ChangelogVo>> getChangelog(@Valid ChangelogFindDto dto) {
        return ApiLocaleResult.success(systemVersionFacade.getChangelog(dto));
    }
    
    // ==================== 更新检查 ====================
    
    @Operation(summary = "检查更新", description = "检查是否有新版本可用")
    @GetMapping("/check-update")
    public ApiLocaleResult<UpdateCheckVo> checkUpdate() {
        return ApiLocaleResult.success(systemVersionFacade.checkUpdate());
    }
    
    // ==================== 系统信息 ====================
    
    @Operation(summary = "获取系统依赖信息", description = "获取系统依赖组件信息")
    @GetMapping("/dependencies")
    public ApiLocaleResult<DependenciesVo> getDependencies() {
        return ApiLocaleResult.success(systemVersionFacade.getDependencies());
    }
    
    @Operation(summary = "获取系统许可证信息", description = "获取系统许可证信息")
    @GetMapping("/license")
    public ApiLocaleResult<LicenseVo> getLicense() {
        return ApiLocaleResult.success(systemVersionFacade.getLicense());
    }
    
    @Operation(summary = "更新许可证", description = "更新系统许可证")
    @PutMapping("/license")
    public ApiLocaleResult<LicenseVo> updateLicense(@Valid @RequestBody LicenseUpdateDto dto) {
        return ApiLocaleResult.success(systemVersionFacade.updateLicense(dto));
    }
    
    @Operation(summary = "获取系统环境信息", description = "获取系统运行环境信息")
    @GetMapping("/environment")
    public ApiLocaleResult<EnvironmentVo> getEnvironment() {
        return ApiLocaleResult.success(systemVersionFacade.getEnvironment());
    }
    
    // ==================== 版本对比 ====================
    
    @Operation(summary = "获取版本对比", description = "对比两个版本之间的差异")
    @GetMapping("/compare")
    public ApiLocaleResult<VersionCompareVo> compareVersions(
            @Parameter(description = "起始版本号") @RequestParam String fromVersion,
            @Parameter(description = "目标版本号") @RequestParam String toVersion) {
        return ApiLocaleResult.success(systemVersionFacade.compareVersions(fromVersion, toVersion));
    }
}
