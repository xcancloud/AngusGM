package cloud.xcan.angus.core.gm.application.query.backup;

import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 备份查询接口
 */
public interface BackupQuery {
    
    Optional<Backup> findById(Long id);
    
    Optional<Backup> findByName(String name);
    
    List<Backup> findByType(BackupType type);
    
    List<Backup> findByStatus(BackupStatus status);
    
    List<Backup> findAll();
    
    Page<Backup> find(GenericSpecification<Backup> spec, Pageable pageable);
}
