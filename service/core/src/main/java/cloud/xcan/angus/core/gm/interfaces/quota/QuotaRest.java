package cloud.xcan.angus.core.gm.interfaces.quota;

import cloud.xcan.angus.commons.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.QuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "配额管理", description = "资源配额管理接口")
@RestController
@RequestMapping("/api/v1/quota")
@RequiredArgsConstructor
public class QuotaRest {
    
    private final QuotaFacade quotaFacade;
    
    @Operation(summary = "创建配额")
    @PostMapping
    public ApiLocaleResult<QuotaDetailVo> create(@RequestBody QuotaCreateDto dto) {
        return ApiLocaleResult.success(quotaFacade.create(dto));
    }
    
    @Operation(summary = "更新配额")
    @PatchMapping
    public ApiLocaleResult<QuotaDetailVo> update(@RequestBody QuotaUpdateDto dto) {
        return ApiLocaleResult.success(quotaFacade.update(dto));
    }
    
    @Operation(summary = "删除配额")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "配额ID") @PathVariable Long id) {
        quotaFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "获取配额详情")
    @GetMapping("/{id}")
    public ApiLocaleResult<QuotaDetailVo> findById(@Parameter(description = "配额ID") @PathVariable Long id) {
        return ApiLocaleResult.success(quotaFacade.findById(id));
    }
    
    @Operation(summary = "查询配额列表")
    @GetMapping
    public ApiLocaleResult<List<QuotaListVo>> findAll(QuotaFindDto dto) {
        return ApiLocaleResult.success(quotaFacade.findAll(dto));
    }
    
    @Operation(summary = "获取配额统计")
    @GetMapping("/stats")
    public ApiLocaleResult<QuotaStatsVo> getStats() {
        return ApiLocaleResult.success(quotaFacade.getStats());
    }
}
