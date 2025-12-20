package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.gm.domain.BaseRepo;

import java.util.List;
import java.util.Optional;

/**
 * 备份仓储接口
 */
public interface BackupRepo extends BaseRepo<Backup> {
    
    Optional<Backup> findByName(String name);
    
    List<Backup> findByType(BackupType type);
    
    List<Backup> findByStatus(BackupStatus status);
    
    long countByType(BackupType type);
    
    long countByStatus(BackupStatus status);
}
