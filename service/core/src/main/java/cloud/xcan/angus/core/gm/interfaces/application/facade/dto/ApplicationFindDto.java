package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import lombok.Data;

/**
 * Application Find DTO
 */
@Data
public class ApplicationFindDto {

    private String name;

    private String code;

    private ApplicationStatus status;

    private ApplicationType type;

    private String ownerId;

    private Integer page;

    private Integer size;
}
