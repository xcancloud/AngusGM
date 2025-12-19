package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import org.springframework.stereotype.Component;

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
}
