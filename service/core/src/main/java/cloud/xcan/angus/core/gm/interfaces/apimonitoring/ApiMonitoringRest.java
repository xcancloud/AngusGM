package cloud.xcan.angus.core.gm.interfaces.apimonitoring;

import cloud.xcan.angus.core.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.ApiMonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringFindDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto.ApiMonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringListVo;
import cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo.ApiMonitoringStatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ApiMonitoring Management", description = "ApiMonitoring management API")
@RestController
@RequestMapping("/api/v1/apimonitoring")
@RequiredArgsConstructor
public class ApiMonitoringRest {
    
    private final ApiMonitoringFacade apiMonitoringFacade;
    
    @Operation(summary = "Create apiMonitoring")
    @PostMapping
    public ApiLocaleResult<ApiMonitoringDetailVo> create(@RequestBody ApiMonitoringCreateDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.create(dto));
    }
    
    @Operation(summary = "Update apiMonitoring")
    @PatchMapping("/{id}")
    public ApiLocaleResult<ApiMonitoringDetailVo> update(
            @Parameter(description = "ApiMonitoring ID") @PathVariable Long id,
            @RequestBody ApiMonitoringUpdateDto dto) {
        return ApiLocaleResult.success(apiMonitoringFacade.update(id, dto));
    }
    
    @Operation(summary = "Delete apiMonitoring")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(
            @Parameter(description = "ApiMonitoring ID") @PathVariable Long id) {
        apiMonitoringFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Enable apiMonitoring")
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(
            @Parameter(description = "ApiMonitoring ID") @PathVariable Long id) {
        apiMonitoringFacade.enable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Disable apiMonitoring")
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(
            @Parameter(description = "ApiMonitoring ID") @PathVariable Long id) {
        apiMonitoringFacade.disable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Get apiMonitoring details")
    @GetMapping("/{id}")
    public ApiLocaleResult<ApiMonitoringDetailVo> findById(
            @Parameter(description = "ApiMonitoring ID") @PathVariable Long id) {
        return ApiLocaleResult.success(apiMonitoringFacade.findById(id));
    }
    
    @Operation(summary = "List apiMonitorings")
    @GetMapping
    public ApiLocaleResult<Page<ApiMonitoringListVo>> find(
            ApiMonitoringFindDto dto,
            Pageable pageable) {
        return ApiLocaleResult.success(apiMonitoringFacade.find(dto, pageable));
    }
    
    @Operation(summary = "Get apiMonitoring statistics")
    @GetMapping("/stats")
    public ApiLocaleResult<ApiMonitoringStatsVo> getStats() {
        return ApiLocaleResult.success(apiMonitoringFacade.getStats());
    }
}
