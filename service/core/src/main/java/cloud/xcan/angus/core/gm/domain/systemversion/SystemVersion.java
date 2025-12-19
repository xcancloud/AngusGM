package cloud.xcan.angus.core.gm.domain.systemversion;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 系统版本实体
 * 系统版本管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_system_version")
public class SystemVersion extends BaseEntity {

    @Column(name = "version_number", length = 50, nullable = false, unique = true)
    private String versionNumber;

    @Column(name = "version_name", length = 100, nullable = false)
    private String versionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private VersionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private VersionStatus status;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "changelog", columnDefinition = "text")
    private String changelog;

    @Column(name = "features", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object features;

    @Column(name = "bug_fixes", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object bugFixes;

    @Column(name = "breaking_changes", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object breakingChanges;

    @Column(name = "rollback_version", length = 50)
    private String rollbackVersion;

    @Column(name = "description", length = 1000)
    private String description;
}
