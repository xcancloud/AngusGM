package cloud.xcan.angus.core.gm.interfaces.systemversion;

import cloud.xcan.angus.core.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.SystemVersionFacade;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionCreateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionFindDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto.SystemVersionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionListVo;
import cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo.SystemVersionStatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SystemVersion Management", description = "SystemVersion management API")
@RestController
@RequestMapping("/api/v1/systemversion")
@RequiredArgsConstructor
public class SystemVersionRest {
    
    private final SystemVersionFacade systemVersionFacade;
    
    @Operation(summary = "Create systemVersion")
    @PostMapping
    public ApiLocaleResult<SystemVersionDetailVo> create(@RequestBody SystemVersionCreateDto dto) {
        return ApiLocaleResult.success(systemVersionFacade.create(dto));
    }
    
    @Operation(summary = "Update systemVersion")
    @PatchMapping("/{id}")
    public ApiLocaleResult<SystemVersionDetailVo> update(
            @Parameter(description = "SystemVersion ID") @PathVariable Long id,
            @RequestBody SystemVersionUpdateDto dto) {
        return ApiLocaleResult.success(systemVersionFacade.update(id, dto));
    }
    
    @Operation(summary = "Delete systemVersion")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(
            @Parameter(description = "SystemVersion ID") @PathVariable Long id) {
        systemVersionFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Enable systemVersion")
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(
            @Parameter(description = "SystemVersion ID") @PathVariable Long id) {
        systemVersionFacade.enable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Disable systemVersion")
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(
            @Parameter(description = "SystemVersion ID") @PathVariable Long id) {
        systemVersionFacade.disable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Get systemVersion details")
    @GetMapping("/{id}")
    public ApiLocaleResult<SystemVersionDetailVo> findById(
            @Parameter(description = "SystemVersion ID") @PathVariable Long id) {
        return ApiLocaleResult.success(systemVersionFacade.findById(id));
    }
    
    @Operation(summary = "List systemVersions")
    @GetMapping
    public ApiLocaleResult<Page<SystemVersionListVo>> find(
            SystemVersionFindDto dto,
            Pageable pageable) {
        return ApiLocaleResult.success(systemVersionFacade.find(dto, pageable));
    }
    
    @Operation(summary = "Get systemVersion statistics")
    @GetMapping("/stats")
    public ApiLocaleResult<SystemVersionStatsVo> getStats() {
        return ApiLocaleResult.success(systemVersionFacade.getStats());
    }
}
