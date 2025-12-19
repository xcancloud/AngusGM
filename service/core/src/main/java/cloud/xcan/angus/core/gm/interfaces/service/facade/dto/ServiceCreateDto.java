package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 服务创建DTO
 */
@Data
public class ServiceCreateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

    @NotNull
    private ServiceProtocol protocol;

    @NotBlank
    private String version;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String applicationId;
}
