package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Application Create DTO
 */
@Data
public class ApplicationCreateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @NotNull
    private ApplicationType type;

    private String description;

    private String homeUrl;

    private String redirectUrl;

    private String ownerId;
}
