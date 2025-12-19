package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 备份实体
 * 备份和恢复管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_backup")
public class Backup extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private BackupType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private BackupStatus status;

    @Column(name = "source_path", length = 500)
    private String sourcePath;

    @Column(name = "backup_path", length = 500, nullable = false)
    private String backupPath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "retention_days")
    private Integer retentionDays;

    @Column(name = "auto_delete", nullable = false)
    private Boolean autoDelete = true;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "description", length = 500)
    private String description;
}
