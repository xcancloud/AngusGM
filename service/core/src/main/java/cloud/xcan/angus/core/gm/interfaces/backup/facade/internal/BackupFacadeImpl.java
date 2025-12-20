package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.backup.BackupCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupStatus;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.BackupAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BackupFacadeImpl implements BackupFacade {
    
    private final BackupCmd backupCmd;
    private final BackupQuery backupQuery;
    private final BackupAssembler assembler;
    
    @Override
    public BackupDetailVo create(BackupCreateDto dto) {
        Backup backup = assembler.toEntity(dto);
        Backup created = backupCmd.create(backup);
        return assembler.toDetailVo(created);
    }
    
    @Override
    public BackupDetailVo update(BackupUpdateDto dto) {
        Backup backup = assembler.toEntity(dto);
        Backup updated = backupCmd.update(backup);
        return assembler.toDetailVo(updated);
    }
    
    @Override
    public void delete(Long id) {
        backupCmd.delete(id);
    }
    
    @Override
    public BackupDetailVo findById(Long id) {
        Backup backup = backupQuery.findById(id).orElseThrow();
        return assembler.toDetailVo(backup);
    }
    
    @Override
    public List<BackupListVo> findAll(BackupFindDto dto) {
        return backupQuery.findAll().stream()
                .map(assembler::toListVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public BackupStatsVo getStats() {
        List<Backup> all = backupQuery.findAll();
        BackupStatsVo stats = new BackupStatsVo();
        stats.setTotalCount((long) all.size());
        stats.setCompletedCount(all.stream().filter(b -> b.getStatus() == BackupStatus.COMPLETED).count());
        stats.setFailedCount(all.stream().filter(b -> b.getStatus() == BackupStatus.FAILED).count());
        stats.setTotalSize(all.stream().mapToLong(b -> b.getFileSize() != null ? b.getFileSize() : 0).sum());
        return stats;
    }
}
