package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupType;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupContentVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreHistoryVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;

import java.util.Set;

public class BackupAssembler {
    
    /**
     * <p>
     * Convert BackupCreateDto to Backup entity
     * </p>
     */
    public static Backup toEntity(BackupCreateDto dto) {
        Backup backup = new Backup();
        backup.setName(dto.getName());
        
        // Convert type string to BackupType enum
        BackupType backupType = parseBackupType(dto.getType());
        backup.setType(backupType);
        
        backup.setDescription(dto.getDescription());
        
        // Set default values
        backup.setStatus(cloud.xcan.angus.core.gm.domain.backup.BackupStatus.PENDING);
        backup.setAutoDelete(true);
        backup.setVerified(false);
        
        // Generate backup path based on type and timestamp
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String typeDir = getTypeDirectory(backupType);
        backup.setBackupPath("/backup/" + typeDir + "/" + timestamp + ".bak");
        
        return backup;
    }
    
    /**
     * <p>
     * Parse backup type string to BackupType enum
     * </p>
     */
    private static BackupType parseBackupType(String type) {
        if (type == null || type.isEmpty()) {
            return BackupType.FULL; // Default to full backup
        }
        
        // Map Chinese type names to enum values
        switch (type) {
            case "完整备份":
                return BackupType.FULL;
            case "增量备份":
                return BackupType.INCREMENTAL;
            case "差异备份":
                return BackupType.DIFFERENTIAL;
            default:
                // Try to parse as enum name
                try {
                    return BackupType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return BackupType.FULL; // Default to full backup
                }
        }
    }
    
    /**
     * <p>
     * Get directory name for backup type
     * </p>
     */
    private static String getTypeDirectory(BackupType type) {
        switch (type) {
            case FULL:
                return "full";
            case INCREMENTAL:
                return "incremental";
            case DIFFERENTIAL:
                return "differential";
            default:
                return "full";
        }
    }
    
    public static BackupDetailVo toDetailVo(Backup backup) {
        BackupDetailVo vo = new BackupDetailVo();
        vo.setId(backup.getId());
        vo.setName(backup.getName());
        vo.setType(backup.getType());
        vo.setStatus(backup.getStatus());
        vo.setSourcePath(backup.getSourcePath());
        vo.setBackupPath(backup.getBackupPath());
        vo.setFileSize(backup.getFileSize());
        vo.setSize(formatFileSize(backup.getFileSize() != null ? backup.getFileSize() : 0));
        vo.setPath(backup.getBackupPath());
        vo.setStartTime(backup.getStartTime());
        vo.setEndTime(backup.getEndTime());
        vo.setDuration(calculateDuration(backup.getStartTime(), backup.getEndTime()));
        vo.setRetentionDays(backup.getRetentionDays());
        vo.setAutoDelete(backup.getAutoDelete());
        vo.setVerified(backup.getVerified());
        vo.setDescription(backup.getDescription());
        vo.setCreatedAt(backup.getCreatedDate());
        
        // 设置审计字段（TenantAuditingVo已继承这些字段，使用BeanUtils或手动设置）
        vo.setTenantId(backup.getTenantId());
        vo.setCreatedBy(backup.getCreatedBy());
        vo.setCreatedDate(backup.getCreatedDate());
        vo.setModifiedBy(backup.getModifiedBy());
        vo.setModifiedDate(backup.getModifiedDate());
        
        // 设置备份内容信息
        BackupContentVo backupContent = new BackupContentVo();
        backupContent.setDatabase(true);
        backupContent.setFiles(true);
        backupContent.setConfigurations(true);
        vo.setBackupContent(backupContent);
        
        // 设置是否可恢复（只有已完成的备份可以恢复）
        vo.setCanRestore(backup.getStatus() == cloud.xcan.angus.core.gm.domain.backup.BackupStatus.COMPLETED);
        
        // 设置恢复历史记录（TODO: 实际实现中应该从数据库获取）
        // vo.setRestoreHistory(restoreHistory);
        
        return vo;
    }
    
    public static BackupListVo toListVo(Backup backup) {
        BackupListVo vo = new BackupListVo();
        vo.setId(backup.getId());
        vo.setName(backup.getName());
        vo.setType(backup.getType());
        vo.setStatus(backup.getStatus());
        vo.setFileSize(backup.getFileSize());
        vo.setSize(formatFileSize(backup.getFileSize() != null ? backup.getFileSize() : 0));
        vo.setPath(backup.getBackupPath());
        vo.setDuration(calculateDuration(backup.getStartTime(), backup.getEndTime()));
        vo.setStartTime(backup.getStartTime());
        vo.setEndTime(backup.getEndTime());
        vo.setCreatedAt(backup.getCreatedDate());
        
        // 设置审计字段（TenantAuditingVo已继承这些字段）
        vo.setTenantId(backup.getTenantId());
        vo.setCreatedBy(backup.getCreatedBy());
        vo.setCreatedDate(backup.getCreatedDate());
        vo.setModifiedBy(backup.getModifiedBy());
        vo.setModifiedDate(backup.getModifiedDate());
        
        return vo;
    }
    
    /**
     * <p>
     * Format file size to human readable string
     * </p>
     */
    private static String formatFileSize(long bytes) {
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
    
    /**
     * <p>
     * Calculate duration between start and end time
     * </p>
     */
    private static String calculateDuration(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return "0分钟";
        }
        
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (minutes < 60) {
            return minutes + "分钟";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + "小时" + (remainingMinutes > 0 ? remainingMinutes + "分钟" : "");
        }
    }
    
    /**
     * <p>
     * Build query specification from FindDto
     * </p>
     */
    public static GenericSpecification<Backup> getSpecification(BackupFindDto dto) {
        Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
            .rangeSearchFields("id", "createdDate")
            .orderByFields("id", "createdDate", "name")
            .matchSearchFields("name", "keyword")
            .equalSearchFields("type", "status")
            .rangeSearchFields("startDate", "endDate")
            .build();
        return new GenericSpecification<>(filters);
    }
}
