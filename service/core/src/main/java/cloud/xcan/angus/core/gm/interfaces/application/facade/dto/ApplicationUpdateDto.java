package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Application Update DTO
 */
@Data
public class ApplicationUpdateDto {

    @NotBlank
    private String id;

    private String name;

    private ApplicationType type;

    private String description;

    private String homeUrl;

    private String redirectUrl;

    private String ownerId;
}
