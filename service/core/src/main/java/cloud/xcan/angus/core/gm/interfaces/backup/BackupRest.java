package cloud.xcan.angus.core.gm.interfaces.backup;

import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupRestoreDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleStatusDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupRestoreVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupStatsVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreProgressVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleRunVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleStatusVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Backup REST Controller
 */
@Tag(name = "Backup", description = "备份管理 - 系统备份、恢复、备份计划管理")
@Validated
@RestController
@RequestMapping("/api/v1/backup")
public class BackupRest {

  @Resource
  private BackupFacade backupFacade;

  @Operation(operationId = "getBackupStats", summary = "获取备份统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<BackupStatsVo> getStats() {
    return ApiLocaleResult.success(backupFacade.getStats());
  }

  @Operation(operationId = "getBackupRecords", summary = "获取备份记录列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份记录列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records")
  public ApiLocaleResult<PageResult<BackupListVo>> listRecords(
      @Valid @ParameterObject BackupFindDto dto) {
    return ApiLocaleResult.success(backupFacade.listRecords(dto));
  }

  @Operation(operationId = "createBackup", summary = "创建备份")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "备份任务已创建")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/records")
  public ApiLocaleResult<BackupDetailVo> createBackup(
      @Valid @RequestBody BackupCreateDto dto) {
    return ApiLocaleResult.success(backupFacade.createBackup(dto));
  }

  @Operation(operationId = "getBackupDetail", summary = "获取备份详情")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份详情获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records/{id}")
  public ApiLocaleResult<BackupDetailVo> getBackupDetail(
      @Parameter(description = "备份ID") @PathVariable String id) {
    return ApiLocaleResult.success(backupFacade.getBackupDetail(id));
  }

  @Operation(operationId = "deleteBackup", summary = "删除备份")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/records/{id}")
  public void deleteBackup(
      @Parameter(description = "备份ID") @PathVariable String id) {
    backupFacade.deleteBackup(id);
  }

  @Operation(operationId = "downloadBackup", summary = "下载备份文件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "文件下载")
  })
  @GetMapping("/records/{id}/download")
  public ResponseEntity<InputStreamResource> downloadBackup(
      @Parameter(description = "备份ID") @PathVariable String id) {
    return backupFacade.downloadBackup(id);
  }

  @Operation(operationId = "restoreBackup", summary = "恢复备份")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "恢复任务已创建")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/records/{id}/restore")
  public ApiLocaleResult<BackupRestoreVo> restoreBackup(
      @Parameter(description = "备份ID") @PathVariable String id,
      @Valid @RequestBody BackupRestoreDto dto) {
    return ApiLocaleResult.success(backupFacade.restoreBackup(id, dto));
  }

  @Operation(operationId = "getRestoreProgress", summary = "获取恢复进度")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "恢复进度获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/restore/{restoreId}/progress")
  public ApiLocaleResult<RestoreProgressVo> getRestoreProgress(
      @Parameter(description = "恢复任务ID") @PathVariable String restoreId) {
    return ApiLocaleResult.success(backupFacade.getRestoreProgress(restoreId));
  }

  @Operation(operationId = "getBackupSchedules", summary = "获取备份计划列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份计划列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/schedules")
  public ApiLocaleResult<List<ScheduleDetailVo>> listSchedules() {
    return ApiLocaleResult.success(backupFacade.listSchedules());
  }

  @Operation(operationId = "createSchedule", summary = "创建备份计划")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "备份计划创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/schedules")
  public ApiLocaleResult<ScheduleDetailVo> createSchedule(
      @Valid @RequestBody ScheduleCreateDto dto) {
    return ApiLocaleResult.success(backupFacade.createSchedule(dto));
  }

  @Operation(operationId = "updateSchedule", summary = "更新备份计划")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份计划更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/schedules/{id}")
  public ApiLocaleResult<ScheduleDetailVo> updateSchedule(
      @Parameter(description = "计划ID") @PathVariable String id,
      @Valid @RequestBody ScheduleUpdateDto dto) {
    return ApiLocaleResult.success(backupFacade.updateSchedule(id, dto));
  }

  @Operation(operationId = "deleteSchedule", summary = "删除备份计划")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/schedules/{id}")
  public void deleteSchedule(
      @Parameter(description = "计划ID") @PathVariable String id) {
    backupFacade.deleteSchedule(id);
  }

  @Operation(operationId = "updateScheduleStatus", summary = "启用/禁用备份计划")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/schedules/{id}/status")
  public ApiLocaleResult<ScheduleStatusVo> updateScheduleStatus(
      @Parameter(description = "计划ID") @PathVariable String id,
      @Valid @RequestBody ScheduleStatusDto dto) {
    return ApiLocaleResult.success(backupFacade.updateScheduleStatus(id, dto));
  }

  @Operation(operationId = "runSchedule", summary = "立即执行备份计划")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份任务已启动")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/schedules/{id}/run")
  public ApiLocaleResult<ScheduleRunVo> runSchedule(
      @Parameter(description = "计划ID") @PathVariable String id) {
    return ApiLocaleResult.success(backupFacade.runSchedule(id));
  }
}
