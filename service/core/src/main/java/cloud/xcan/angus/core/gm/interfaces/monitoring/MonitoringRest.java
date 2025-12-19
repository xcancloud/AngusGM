package cloud.xcan.angus.core.gm.interfaces.monitoring;

import cloud.xcan.angus.core.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.MonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringCreateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringFindDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto.MonitoringUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringDetailVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringListVo;
import cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo.MonitoringStatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Monitoring Management", description = "Monitoring management API")
@RestController
@RequestMapping("/api/v1/monitoring")
@RequiredArgsConstructor
public class MonitoringRest {
    
    private final MonitoringFacade monitoringFacade;
    
    @Operation(summary = "Create monitoring")
    @PostMapping
    public ApiLocaleResult<MonitoringDetailVo> create(@RequestBody MonitoringCreateDto dto) {
        return ApiLocaleResult.success(monitoringFacade.create(dto));
    }
    
    @Operation(summary = "Update monitoring")
    @PatchMapping("/{id}")
    public ApiLocaleResult<MonitoringDetailVo> update(
            @Parameter(description = "Monitoring ID") @PathVariable Long id,
            @RequestBody MonitoringUpdateDto dto) {
        return ApiLocaleResult.success(monitoringFacade.update(id, dto));
    }
    
    @Operation(summary = "Delete monitoring")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(
            @Parameter(description = "Monitoring ID") @PathVariable Long id) {
        monitoringFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Enable monitoring")
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(
            @Parameter(description = "Monitoring ID") @PathVariable Long id) {
        monitoringFacade.enable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Disable monitoring")
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(
            @Parameter(description = "Monitoring ID") @PathVariable Long id) {
        monitoringFacade.disable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Get monitoring details")
    @GetMapping("/{id}")
    public ApiLocaleResult<MonitoringDetailVo> findById(
            @Parameter(description = "Monitoring ID") @PathVariable Long id) {
        return ApiLocaleResult.success(monitoringFacade.findById(id));
    }
    
    @Operation(summary = "List monitorings")
    @GetMapping
    public ApiLocaleResult<Page<MonitoringListVo>> find(
            MonitoringFindDto dto,
            Pageable pageable) {
        return ApiLocaleResult.success(monitoringFacade.find(dto, pageable));
    }
    
    @Operation(summary = "Get monitoring statistics")
    @GetMapping("/stats")
    public ApiLocaleResult<MonitoringStatsVo> getStats() {
        return ApiLocaleResult.success(monitoringFacade.getStats());
    }
}
