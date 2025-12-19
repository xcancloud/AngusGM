package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Application Detail VO
 */
@Data
public class ApplicationDetailVo {

    private String id;

    private String name;

    private String code;

    private ApplicationType type;

    private ApplicationStatus status;

    private String description;

    private String clientId;

    private String clientSecret;

    private String homeUrl;

    private String redirectUrl;

    private String ownerId;

    private String ownerName;

    private Integer serviceCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
