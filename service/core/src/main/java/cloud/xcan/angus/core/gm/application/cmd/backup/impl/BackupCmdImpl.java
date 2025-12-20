package cloud.xcan.angus.core.gm.application.cmd.backup.impl;

import cloud.xcan.angus.core.gm.application.cmd.backup.BackupCmd;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 备份命令实现
 */
@Service
@RequiredArgsConstructor
public class BackupCmdImpl implements BackupCmd {
    
    private final BackupRepo backupRepo;
    
    @Override
    @Transactional
    public Backup create(Backup backup) {
        backup.setStatus(BackupStatus.PENDING);
        return backupRepo.save(backup);
    }
    
    @Override
    @Transactional
    public Backup update(Backup backup) {
        return backupRepo.save(backup);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        backupRepo.deleteById(id);
    }
    
    @Override
    @Transactional
    public void enable(Long id) {
        Backup backup = backupRepo.findById(id).orElseThrow();
        backup.setAutoDelete(true);
        backupRepo.save(backup);
    }
    
    @Override
    @Transactional
    public void disable(Long id) {
        Backup backup = backupRepo.findById(id).orElseThrow();
        backup.setAutoDelete(false);
        backupRepo.save(backup);
    }
    
    @Override
    @Transactional
    public void startBackup(Long id) {
        Backup backup = backupRepo.findById(id).orElseThrow();
        backup.setStatus(BackupStatus.IN_PROGRESS);
        backup.setStartTime(LocalDateTime.now());
        backupRepo.save(backup);
    }
    
    @Override
    @Transactional
    public void restore(Long id) {
        Backup backup = backupRepo.findById(id).orElseThrow();
        backup.setStatus(BackupStatus.RESTORING);
        backupRepo.save(backup);
    }
}
