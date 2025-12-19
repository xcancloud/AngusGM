package cloud.xcan.angus.core.gm.interfaces.auditlog;

import cloud.xcan.angus.core.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.AuditLogFacade;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogCreateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto.AuditLogUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogListVo;
import cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo.AuditLogStatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AuditLog Management", description = "AuditLog management API")
@RestController
@RequestMapping("/api/v1/auditlog")
@RequiredArgsConstructor
public class AuditLogRest {
    
    private final AuditLogFacade auditLogFacade;
    
    @Operation(summary = "Create auditLog")
    @PostMapping
    public ApiLocaleResult<AuditLogDetailVo> create(@RequestBody AuditLogCreateDto dto) {
        return ApiLocaleResult.success(auditLogFacade.create(dto));
    }
    
    @Operation(summary = "Update auditLog")
    @PatchMapping("/{id}")
    public ApiLocaleResult<AuditLogDetailVo> update(
            @Parameter(description = "AuditLog ID") @PathVariable Long id,
            @RequestBody AuditLogUpdateDto dto) {
        return ApiLocaleResult.success(auditLogFacade.update(id, dto));
    }
    
    @Operation(summary = "Delete auditLog")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(
            @Parameter(description = "AuditLog ID") @PathVariable Long id) {
        auditLogFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Enable auditLog")
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(
            @Parameter(description = "AuditLog ID") @PathVariable Long id) {
        auditLogFacade.enable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Disable auditLog")
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(
            @Parameter(description = "AuditLog ID") @PathVariable Long id) {
        auditLogFacade.disable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Get auditLog details")
    @GetMapping("/{id}")
    public ApiLocaleResult<AuditLogDetailVo> findById(
            @Parameter(description = "AuditLog ID") @PathVariable Long id) {
        return ApiLocaleResult.success(auditLogFacade.findById(id));
    }
    
    @Operation(summary = "List auditLogs")
    @GetMapping
    public ApiLocaleResult<Page<AuditLogListVo>> find(
            AuditLogFindDto dto,
            Pageable pageable) {
        return ApiLocaleResult.success(auditLogFacade.find(dto, pageable));
    }
    
    @Operation(summary = "Get auditLog statistics")
    @GetMapping("/stats")
    public ApiLocaleResult<AuditLogStatsVo> getStats() {
        return ApiLocaleResult.success(auditLogFacade.getStats());
    }
}
