package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import cloud.xcan.angus.core.gm.domain.service.enums.ServiceProtocol;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 服务更新DTO
 */
@Data
public class ServiceUpdateDto {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

    private ServiceProtocol protocol;

    private String version;

    private String baseUrl;

    private String applicationId;
}
