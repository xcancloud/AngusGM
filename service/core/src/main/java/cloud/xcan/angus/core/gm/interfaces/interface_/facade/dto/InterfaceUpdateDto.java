package cloud.xcan.angus.core.gm.interfaces.interface_.facade.dto;

import cloud.xcan.angus.core.gm.domain.interface_.enums.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterfaceUpdateDto {
    private String id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String path;
    
    private HttpMethod method;
    
    private String description;
    
    private Boolean authRequired;
    
    private String serviceId;
}
