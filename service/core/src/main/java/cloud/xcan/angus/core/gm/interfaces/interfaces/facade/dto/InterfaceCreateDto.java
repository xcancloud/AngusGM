package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto;

import cloud.xcan.angus.core.gm.domain.interfaces.enums.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterfaceCreateDto {
    @NotBlank
    private String name;
    
    @NotBlank
    private String path;
    
    @NotNull
    private HttpMethod method;
    
    private String description;
    
    private Boolean authRequired = false;
    
    @NotBlank
    private String serviceId;
}
