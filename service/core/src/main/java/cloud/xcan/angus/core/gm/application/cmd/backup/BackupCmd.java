package cloud.xcan.angus.core.gm.application.cmd.backup;

import cloud.xcan.angus.core.gm.domain.backup.Backup;

/**
 * 备份命令接口
 */
public interface BackupCmd {
    
    Backup create(Backup backup);
    
    Backup update(Backup backup);
    
    void delete(Long id);
    
    void enable(Long id);
    
    void disable(Long id);
    
    void startBackup(Long id);
    
    void restore(Long id);
}
