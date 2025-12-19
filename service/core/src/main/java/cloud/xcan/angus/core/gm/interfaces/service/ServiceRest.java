package cloud.xcan.angus.core.gm.interfaces.service;

import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceCreateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
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
 * 服务管理REST接口
 */
@Tag(name = "Service", description = "服务管理 - 服务的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceRest {

    private final ServiceFacade serviceFacade;

    // 创建
    @Operation(operationId = "createService", summary = "创建服务", description = "创建新服务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "服务创建成功")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiLocaleResult<ServiceDetailVo> create(@Valid @RequestBody ServiceCreateDto dto) {
        return ApiLocaleResult.success(serviceFacade.create(dto));
    }

    // 更新
    @Operation(operationId = "updateService", summary = "更新服务", description = "更新服务基本信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public ApiLocaleResult<ServiceDetailVo> update(
        @Parameter(description = "服务ID") @PathVariable String id,
        @Valid @RequestBody ServiceUpdateDto dto) {
        dto.setId(id);
        return ApiLocaleResult.success(serviceFacade.update(dto));
    }

    // 修改状态 - 启用
    @Operation(operationId = "enableService", summary = "启用服务", description = "启用指定服务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "启用成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(@Parameter(description = "服务ID") @PathVariable String id) {
        serviceFacade.enable(id);
        return ApiLocaleResult.success(null);
    }

    // 修改状态 - 禁用
    @Operation(operationId = "disableService", summary = "禁用服务", description = "禁用指定服务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "禁用成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(@Parameter(description = "服务ID") @PathVariable String id) {
        serviceFacade.disable(id);
        return ApiLocaleResult.success(null);
    }

    // 删除
    @Operation(operationId = "deleteService", summary = "删除服务", description = "删除指定服务")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "服务ID") @PathVariable String id) {
        serviceFacade.delete(id);
        return ApiLocaleResult.success(null);
    }

    // 查询详细
    @Operation(operationId = "getService", summary = "查询服务详情", description = "根据ID查询服务详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ApiLocaleResult<ServiceDetailVo> get(@Parameter(description = "服务ID") @PathVariable String id) {
        return ApiLocaleResult.success(serviceFacade.get(id));
    }

    // 查询列表
    @Operation(operationId = "findServices", summary = "查询服务列表", description = "分页查询服务列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ApiLocaleResult<Page<ServiceListVo>> find(@ParameterObject ServiceFindDto dto) {
        return ApiLocaleResult.success(serviceFacade.find(dto));
    }

    // 查询统计
    @Operation(operationId = "getServiceStats", summary = "查询服务统计", description = "获取服务统计信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/stats")
    public ApiLocaleResult<ServiceStatsVo> stats() {
        return ApiLocaleResult.success(serviceFacade.stats());
    }
}
