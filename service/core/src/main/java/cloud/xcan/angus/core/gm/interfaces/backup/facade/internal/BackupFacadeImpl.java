package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.backup.BackupCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.BackupAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.*;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cloud.xcan.angus.remote.PageResult;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

@Component
public class BackupFacadeImpl implements BackupFacade {
    
    @Resource
    private BackupCmd backupCmd;
    
    @Resource
    private BackupQuery backupQuery;
    
    // ==================== 备份记录 ====================
    
    @Override
    public BackupDetailVo createBackup(BackupCreateDto dto) {
        // 使用Assembler将DTO转换为Domain对象
        Backup backup = BackupAssembler.toEntity(dto);
        // 调用Cmd服务创建备份
        Backup created = backupCmd.create(backup);
        // 使用Assembler将Domain对象转换为VO
        return BackupAssembler.toDetailVo(created);
    }
    
    @Override
    public BackupDetailVo getBackupDetail(Long id) {
        // 调用Query服务根据ID查找备份
        Backup backup = backupQuery.findById(id)
            .orElseThrow(() -> cloud.xcan.angus.remote.message.http.ResourceNotFound.of("备份记录不存在", new Object[]{}));
        // 使用Assembler将Domain对象转换为VO
        return BackupAssembler.toDetailVo(backup);
    }
    
    @Override
    public void deleteBackup(Long id) {
        // 调用Cmd服务删除备份
        backupCmd.delete(id);
    }
    
    @Override
    public ResponseEntity<InputStreamResource> downloadBackup(Long id) {
        // 查找备份记录
        Backup backup = backupQuery.findById(id)
            .orElseThrow(() -> cloud.xcan.angus.remote.message.http.ResourceNotFound.of("备份记录不存在", new Object[]{}));
        
        // 检查备份文件是否存在
        if (backup.getBackupPath() == null || backup.getBackupPath().isEmpty()) {
            throw cloud.xcan.angus.remote.message.http.ResourceNotFound.of("备份文件不存在", new Object[]{});
        }
        
        // TODO: 实际实现中需要从文件系统读取文件并返回流
        // 这里返回一个占位实现，实际应该使用FileSystemResource或类似的方式
        throw new UnsupportedOperationException("文件下载功能待实现");
    }
    
    @Override
    public BackupRestoreVo restoreBackup(Long id, BackupRestoreDto dto) {
        // 查找备份记录
        Backup backup = backupQuery.findById(id)
            .orElseThrow(() -> cloud.xcan.angus.remote.message.http.ResourceNotFound.of("备份记录不存在", new Object[]{}));
        
        // 检查备份状态
        if (backup.getStatus() != BackupStatus.COMPLETED) {
            throw new IllegalArgumentException("只能恢复已完成的备份");
        }
        
        // 调用Cmd服务恢复备份
        backupCmd.restore(id);
        
        // 生成恢复任务ID
        Long restoreId = System.currentTimeMillis();
        
        // 构造返回VO
        BackupRestoreVo vo = new BackupRestoreVo();
        vo.setBackupId(id);
        vo.setRestoreId(restoreId);
        vo.setStatus("进行中");
        vo.setStartTime(java.time.LocalDateTime.now());
        
        return vo;
    }
    
    @Override
    public PageResult<BackupListVo> listRecords(BackupFindDto dto) {
        // 构建查询条件
        GenericSpecification<Backup> spec = BackupAssembler.getSpecification(dto);
        
        // 调用Query服务进行分页查询
        Page<Backup> page = backupQuery.find(spec, dto.tranPage());
        
        // 使用buildVoPageResult构建分页结果
        return buildVoPageResult(page, BackupAssembler::toListVo);
    }
    
    // ==================== 备份统计 ====================
    
    @Override
    public BackupStatsVo getStats() {
        List<Backup> all = backupQuery.findAll();
        BackupStatsVo stats = new BackupStatsVo();
        stats.setTotalBackups((long) all.size());
        stats.setSuccessBackups(all.stream().filter(b -> b.getStatus() == BackupStatus.COMPLETED).count());
        stats.setFailedBackups(all.stream().filter(b -> b.getStatus() == BackupStatus.FAILED).count());
        
        // 计算总大小（字节）
        long totalSizeBytes = all.stream()
            .mapToLong(b -> b.getFileSize() != null ? b.getFileSize() : 0)
            .sum();
        
        // 格式化总大小
        stats.setTotalSize(formatFileSize(totalSizeBytes));
        
        // 设置最后备份时间
        if (!all.isEmpty()) {
            java.time.LocalDateTime lastBackupTime = all.stream()
                .map(Backup::getCreatedDate)
                .filter(date -> date != null)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
            if (lastBackupTime != null) {
                stats.setLastBackupTime(lastBackupTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }
        
        return stats;
    }
    
    @Override
    public RestoreProgressVo getRestoreProgress(Long restoreId) {
        // TODO: 实际实现中应该从恢复任务存储中获取进度信息
        // 这里返回一个模拟实现
        RestoreProgressVo vo = new RestoreProgressVo();
        vo.setRestoreId(restoreId);
        vo.setStatus("进行中");
        vo.setProgress(45); // 模拟进度45%
        vo.setCurrentStep("恢复数据库");
        vo.setTotalSteps(3);
        vo.setCompletedSteps(1);
        vo.setStartTime(java.time.LocalDateTime.now().minusMinutes(10)); // 模拟开始时间10分钟前
        vo.setEstimatedEndTime(java.time.LocalDateTime.now().plusMinutes(10)); // 模拟预计结束时间10分钟后
        
        return vo;
    }
    
    // ==================== 备份计划 ====================
    
    @Override
    public ScheduleDetailVo createSchedule(ScheduleCreateDto dto) {
        // 模拟创建备份计划，等待后续实现
        ScheduleDetailVo vo = new ScheduleDetailVo();
        vo.setId(System.currentTimeMillis()); // 生成临时ID
        vo.setName(dto.getName());
        vo.setType(dto.getType());
        vo.setFrequency(dto.getFrequency());
        vo.setTime(dto.getTime());
        vo.setRetention(dto.getRetention());
        vo.setStatus(dto.getStatus());
        vo.setCreatedAt(java.time.LocalDateTime.now());
        
        return vo;
    }
    
    @Override
    public ScheduleDetailVo updateSchedule(Long id, ScheduleUpdateDto dto) {
        // 模拟更新备份计划，等待后续实现
        ScheduleDetailVo vo = new ScheduleDetailVo();
        vo.setId(id);
        vo.setName(dto.getName());
        vo.setType(dto.getType());
        vo.setFrequency(dto.getFrequency());
        vo.setTime(dto.getTime());
        vo.setRetention(dto.getRetention());
        vo.setStatus(dto.getStatus());
        vo.setCreatedAt(java.time.LocalDateTime.now());
        
        return vo;
    }
    
    @Override
    public ScheduleStatusVo updateScheduleStatus(Long id, ScheduleStatusDto dto) {
        // 模拟更新备份计划状态，等待后续实现
        ScheduleStatusVo vo = new ScheduleStatusVo();
        vo.setId(id);
        vo.setStatus(dto.getStatus());
        vo.setModifiedDate(java.time.LocalDateTime.now());
        
        return vo;
    }
    
    @Override
    public ScheduleRunVo runSchedule(Long id) {
        // TODO: 实际实现中应该调用备份计划执行服务
        // 这里返回一个模拟实现
        ScheduleRunVo vo = new ScheduleRunVo();
        vo.setScheduleId(id);
        vo.setBackupId(System.currentTimeMillis()); // 生成备份ID
        vo.setStartTime(java.time.LocalDateTime.now());
        
        return vo;
    }
    
    /**
     * <p>
     * Format file size to human readable string
     * </p>
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    @Override
    public List<ScheduleDetailVo> listSchedules() {
        // 模拟返回空列表，等待后续实现
        return new ArrayList<>();
    }
    
    @Override
    public void deleteSchedule(Long id) {
        // 模拟删除备份计划，等待后续实现
        // 实际实现中应该调用相应的Cmd服务
    }
}