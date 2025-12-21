package cloud.xcan.angus.core.gm.interfaces.backup.facade;

import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.*;

import java.util.List;

public interface BackupFacade {
    
    // ==================== 备份记录 ====================
    
    BackupDetailVo createBackup(BackupCreateDto dto);
    
    BackupDetailVo getBackupDetail(Long id);
    
    BackupDetailVo updateBackup(Long id, BackupUpdateDto dto);
    
    void deleteBackup(Long id);
    
    org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource> downloadBackup(Long id);
    
    BackupRestoreVo restoreBackup(Long id, BackupRestoreDto dto);
    
    PageResult<BackupListVo> listRecords(BackupFindDto dto);
    
    // ==================== 备份统计 ====================
    
    BackupStatsVo getStats();
    
    RestoreProgressVo getRestoreProgress(String restoreId);
    
    // ==================== 备份计划 ====================
    
    ScheduleDetailVo createSchedule(ScheduleCreateDto dto);
    
    ScheduleDetailVo updateSchedule(Long id, ScheduleUpdateDto dto);
    
    ScheduleStatusVo updateScheduleStatus(Long id, ScheduleStatusDto dto);
    
    ScheduleRunVo runSchedule(Long id);
    
    List<ScheduleDetailVo> listSchedules();
    
    void deleteSchedule(Long id);
}
