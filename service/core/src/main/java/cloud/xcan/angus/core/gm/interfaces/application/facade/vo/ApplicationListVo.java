package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Application List VO
 */
@Data
public class ApplicationListVo {

    private String id;

    private String name;

    private String code;

    private ApplicationType type;

    private ApplicationStatus status;

    private String description;

    private String ownerId;

    private String ownerName;

    private Integer serviceCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
