package cloud.xcan.angus.core.gm.interfaces.interface_;

import cloud.xcan.angus.core.gm.interfaces.interface_.facade.InterfaceFacade;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto.InterfaceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo.InterfaceStatsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 接口管理REST接口
 */
@Tag(name = "Interface", description = "接口管理 - 接口的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/interfaces")
@RequiredArgsConstructor
public class InterfaceRest {

    private final InterfaceFacade interfaceFacade;

    // 创建
    @Operation(operationId = "createInterface", summary = "创建接口", description = "创建新接口")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "接口创建成功")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiLocaleResult<InterfaceDetailVo> create(@Valid @RequestBody InterfaceCreateDto dto) {
        return ApiLocaleResult.success(interfaceFacade.create(dto));
    }

    // 更新
    @Operation(operationId = "updateInterface", summary = "更新接口", description = "更新接口基本信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public ApiLocaleResult<InterfaceDetailVo> update(
        @Parameter(description = "接口ID") @PathVariable String id,
        @Valid @RequestBody InterfaceUpdateDto dto) {
        dto.setId(id);
        return ApiLocaleResult.success(interfaceFacade.update(dto));
    }

    // 修改状态 - 启用
    @Operation(operationId = "enableInterface", summary = "启用接口", description = "启用指定接口")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "启用成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(@Parameter(description = "接口ID") @PathVariable String id) {
        interfaceFacade.enable(id);
        return ApiLocaleResult.success(null);
    }

    // 修改状态 - 禁用
    @Operation(operationId = "disableInterface", summary = "禁用接口", description = "禁用指定接口")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "禁用成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(@Parameter(description = "接口ID") @PathVariable String id) {
        interfaceFacade.disable(id);
        return ApiLocaleResult.success(null);
    }

    // 删除
    @Operation(operationId = "deleteInterface", summary = "删除接口", description = "删除指定接口")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "接口ID") @PathVariable String id) {
        interfaceFacade.delete(id);
        return ApiLocaleResult.success(null);
    }

    // 查询详细
    @Operation(operationId = "getInterface", summary = "查询接口详情", description = "根据ID查询接口详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ApiLocaleResult<InterfaceDetailVo> get(@Parameter(description = "接口ID") @PathVariable String id) {
        return ApiLocaleResult.success(interfaceFacade.get(id));
    }

    // 查询列表
    @Operation(operationId = "findInterfaces", summary = "查询接口列表", description = "分页查询接口列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ApiLocaleResult<Page<InterfaceListVo>> find(@ParameterObject InterfaceFindDto dto) {
        return ApiLocaleResult.success(interfaceFacade.find(dto));
    }

    // 查询统计
    @Operation(operationId = "getInterfaceStats", summary = "查询接口统计", description = "获取接口统计信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stats")
    public ApiLocaleResult<InterfaceStatsVo> stats() {
        return ApiLocaleResult.success(interfaceFacade.stats());
    }
}
