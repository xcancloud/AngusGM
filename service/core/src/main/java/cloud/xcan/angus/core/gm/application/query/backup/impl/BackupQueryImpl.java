package cloud.xcan.angus.core.gm.application.query.backup.impl;

import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupRepo;
import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 备份查询实现
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupQueryImpl implements BackupQuery {
    
    private final BackupRepo backupRepo;
    
    @Override
    public Optional<Backup> findById(Long id) {
        return backupRepo.findById(id);
    }
    
    @Override
    public Optional<Backup> findByName(String name) {
        return backupRepo.findByName(name);
    }
    
    @Override
    public List<Backup> findByType(BackupType type) {
        return backupRepo.findByType(type);
    }
    
    @Override
    public List<Backup> findByStatus(BackupStatus status) {
        return backupRepo.findByStatus(status);
    }
    
    @Override
    public List<Backup> findAll() {
        return backupRepo.findAll();
    }
    
    @Override
    public Page<Backup> find(GenericSpecification<Backup> spec, Pageable pageable) {
        return backupRepo.findAll(spec, pageable);
    }
}
