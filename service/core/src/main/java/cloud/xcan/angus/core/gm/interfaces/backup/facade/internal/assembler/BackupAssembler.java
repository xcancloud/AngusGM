package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupContentVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreHistoryVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BackupAssembler {
    
    public Backup toEntity(BackupCreateDto dto) {
        Backup backup = new Backup();
        backup.setName(dto.getName());
        backup.setType(dto.getType());
        backup.setSourcePath(dto.getSourcePath());
        backup.setBackupPath(dto.getBackupPath());
        backup.setRetentionDays(dto.getRetentionDays());
        backup.setAutoDelete(dto.getAutoDelete());
        backup.setDescription(dto.getDescription());
        return backup;
    }
    
    public Backup toEntity(BackupUpdateDto dto) {
        Backup backup = new Backup();
        backup.setId(dto.getId());
        backup.setName(dto.getName());
        backup.setStatus(dto.getStatus());
        backup.setRetentionDays(dto.getRetentionDays());
        backup.setAutoDelete(dto.getAutoDelete());
        backup.setDescription(dto.getDescription());
        return backup;
    }
    
    public BackupDetailVo toDetailVo(Backup backup) {
        BackupDetailVo vo = new BackupDetailVo();
        vo.setId(backup.getId());
        vo.setName(backup.getName());
        vo.setType(backup.getType());
        vo.setStatus(backup.getStatus());
        vo.setSourcePath(backup.getSourcePath());
        vo.setBackupPath(backup.getBackupPath());
        vo.setFileSize(backup.getFileSize());
        vo.setStartTime(backup.getStartTime());
        vo.setEndTime(backup.getEndTime());
        vo.setRetentionDays(backup.getRetentionDays());
        vo.setAutoDelete(backup.getAutoDelete());
        vo.setVerified(backup.getVerified());
        vo.setDescription(backup.getDescription());
        vo.setCreatedAt(backup.getCreatedAt());
        
        // 设置备份内容信息
        BackupContentVo backupContent = new BackupContentVo();
        backupContent.setDatabase(true);
        backupContent.setFiles(true);
        backupContent.setConfigurations(true);
        vo.setBackupContent(backupContent);
        
        // 设置是否可恢复
        vo.setCanRestore(true);
        
        // 设置恢复历史记录（模拟数据）
        // 在实际实现中，这里应该从数据库或其他存储中获取真实的恢复历史记录
        // vo.setRestoreHistory(restoreHistory);
        
        return vo;
    }
    
    public BackupListVo toListVo(Backup backup) {
        BackupListVo vo = new BackupListVo();
        vo.setId(backup.getId());
        vo.setName(backup.getName());
        vo.setType(backup.getType());
        vo.setStatus(backup.getStatus());
        vo.setFileSize(backup.getFileSize());
        vo.setCreatedAt(backup.getCreatedAt());
        return vo;
    }
    
    /**
     * Build query specification from FindDto
     */
    public static GenericSpecification<Backup> getSpecification(BackupFindDto dto) {
        Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
            .rangeSearchFields("id", "createdDate")
            .orderByFields("id", "createdDate", "name")
            .matchSearchFields("name")
            .equalSearchFields("type", "status")
            .rangeSearchFields("startDate", "endDate")
            .build();
        return new GenericSpecification<>(filters);
    }
}
