package cloud.xcan.angus.core.gm.interfaces.systemversion;

import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.SystemVersionFacade;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.ChangelogFindDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.LicenseUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.VersionHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.ChangelogVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.CurrentVersionVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.DependenciesVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.EnvironmentVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.LicenseVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.UpdateCheckVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.VersionCompareVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.VersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.VersionHistoryVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * System Version REST API
 */
@Tag(name = "SystemVersion", description = "系统版本 - 系统版本信息、更新管理、变更日志")
@Validated
@RestController
@RequestMapping("/api/v1/system-version")
public class SystemVersionRest {

  @Resource
  private SystemVersionFacade systemVersionFacade;

  @Operation(operationId = "updateLicense", summary = "更新许可证", description = "更新系统许可证")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "许可证无效")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/license")
  public ApiLocaleResult<LicenseVo> updateLicense(@Valid @RequestBody LicenseUpdateDto dto) {
    return ApiLocaleResult.success(systemVersionFacade.updateLicense(dto));
  }

  @Operation(operationId = "getCurrentVersion", summary = "获取当前系统版本信息", description = "获取当前运行的系统版本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/current")
  public ApiLocaleResult<CurrentVersionVo> getCurrentVersion() {
    return ApiLocaleResult.success(systemVersionFacade.getCurrentVersion());
  }

  @Operation(operationId = "listVersionHistory", summary = "获取版本历史列表", description = "分页获取版本历史列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/history")
  public ApiLocaleResult<PageResult<VersionHistoryVo>> listVersionHistory(@Valid VersionHistoryFindDto dto) {
    return ApiLocaleResult.success(systemVersionFacade.listVersionHistory(dto));
  }

  @Operation(operationId = "getVersionDetail", summary = "获取版本详情", description = "获取指定版本的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/history/{id}")
  public ApiLocaleResult<VersionDetailVo> getVersionDetail(
      @Parameter(description = "版本ID") @PathVariable String id) {
    return ApiLocaleResult.success(systemVersionFacade.getVersionDetail(id));
  }

  @Operation(operationId = "getChangelog", summary = "获取变更日志", description = "获取版本变更日志")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/changelog")
  public ApiLocaleResult<PageResult<ChangelogVo>> getChangelog(@Valid ChangelogFindDto dto) {
    return ApiLocaleResult.success(systemVersionFacade.getChangelog(dto));
  }

  @Operation(operationId = "checkUpdate", summary = "检查更新", description = "检查是否有新版本可用")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "检查成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/check-update")
  public ApiLocaleResult<UpdateCheckVo> checkUpdate() {
    return ApiLocaleResult.success(systemVersionFacade.checkUpdate());
  }

  @Operation(operationId = "getDependencies", summary = "获取系统依赖信息", description = "获取系统依赖组件信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/dependencies")
  public ApiLocaleResult<DependenciesVo> getDependencies() {
    return ApiLocaleResult.success(systemVersionFacade.getDependencies());
  }

  @Operation(operationId = "getLicense", summary = "获取系统许可证信息", description = "获取系统许可证信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/license")
  public ApiLocaleResult<LicenseVo> getLicense() {
    return ApiLocaleResult.success(systemVersionFacade.getLicense());
  }

  @Operation(operationId = "getEnvironment", summary = "获取系统环境信息", description = "获取系统运行环境信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/environment")
  public ApiLocaleResult<EnvironmentVo> getEnvironment() {
    return ApiLocaleResult.success(systemVersionFacade.getEnvironment());
  }

  @Operation(operationId = "compareVersions", summary = "获取版本对比", description = "对比两个版本之间的差异")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "对比成功"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/compare")
  public ApiLocaleResult<VersionCompareVo> compareVersions(
      @Parameter(description = "起始版本号") @RequestParam String fromVersion,
      @Parameter(description = "目标版本号") @RequestParam String toVersion) {
    return ApiLocaleResult.success(systemVersionFacade.compareVersions(fromVersion, toVersion));
  }
}
