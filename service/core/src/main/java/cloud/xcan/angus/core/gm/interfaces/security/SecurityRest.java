package cloud.xcan.angus.core.gm.interfaces.security;

import cloud.xcan.angus.core.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.security.facade.SecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityDetailVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityListVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityStatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Security Management", description = "Security management API")
@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class SecurityRest {
    
    private final SecurityFacade securityFacade;
    
    @Operation(summary = "Create security")
    @PostMapping
    public ApiLocaleResult<SecurityDetailVo> create(@RequestBody SecurityCreateDto dto) {
        return ApiLocaleResult.success(securityFacade.create(dto));
    }
    
    @Operation(summary = "Update security")
    @PatchMapping("/{id}")
    public ApiLocaleResult<SecurityDetailVo> update(
            @Parameter(description = "Security ID") @PathVariable Long id,
            @RequestBody SecurityUpdateDto dto) {
        return ApiLocaleResult.success(securityFacade.update(id, dto));
    }
    
    @Operation(summary = "Delete security")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(
            @Parameter(description = "Security ID") @PathVariable Long id) {
        securityFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Enable security")
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(
            @Parameter(description = "Security ID") @PathVariable Long id) {
        securityFacade.enable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Disable security")
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(
            @Parameter(description = "Security ID") @PathVariable Long id) {
        securityFacade.disable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Get security details")
    @GetMapping("/{id}")
    public ApiLocaleResult<SecurityDetailVo> findById(
            @Parameter(description = "Security ID") @PathVariable Long id) {
        return ApiLocaleResult.success(securityFacade.findById(id));
    }
    
    @Operation(summary = "List securitys")
    @GetMapping
    public ApiLocaleResult<Page<SecurityListVo>> find(
            SecurityFindDto dto,
            Pageable pageable) {
        return ApiLocaleResult.success(securityFacade.find(dto, pageable));
    }
    
    @Operation(summary = "Get security statistics")
    @GetMapping("/stats")
    public ApiLocaleResult<SecurityStatsVo> getStats() {
        return ApiLocaleResult.success(securityFacade.getStats());
    }
}
