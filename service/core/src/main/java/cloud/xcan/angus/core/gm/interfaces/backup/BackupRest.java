package cloud.xcan.angus.core.gm.interfaces.backup;

import cloud.xcan.angus.commons.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "备份管理", description = "备份和恢复管理接口")
@RestController
@RequestMapping("/api/v1/backup")
@RequiredArgsConstructor
public class BackupRest {
    
    private final BackupFacade backupFacade;
    
    @Operation(summary = "创建备份")
    @PostMapping
    public ApiLocaleResult<BackupDetailVo> create(@RequestBody BackupCreateDto dto) {
        return ApiLocaleResult.success(backupFacade.create(dto));
    }
    
    @Operation(summary = "更新备份")
    @PatchMapping
    public ApiLocaleResult<BackupDetailVo> update(@RequestBody BackupUpdateDto dto) {
        return ApiLocaleResult.success(backupFacade.update(dto));
    }
    
    @Operation(summary = "删除备份")
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "备份ID") @PathVariable Long id) {
        backupFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "获取备份详情")
    @GetMapping("/{id}")
    public ApiLocaleResult<BackupDetailVo> findById(@Parameter(description = "备份ID") @PathVariable Long id) {
        return ApiLocaleResult.success(backupFacade.findById(id));
    }
    
    @Operation(summary = "查询备份列表")
    @GetMapping
    public ApiLocaleResult<List<BackupListVo>> findAll(BackupFindDto dto) {
        return ApiLocaleResult.success(backupFacade.findAll(dto));
    }
    
    @Operation(summary = "获取备份统计")
    @GetMapping("/stats")
    public ApiLocaleResult<BackupStatsVo> getStats() {
        return ApiLocaleResult.success(backupFacade.getStats());
    }
}
