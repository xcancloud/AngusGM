package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SecurityCreateDto implements Serializable {
    private String name;
    private String description;
    private Boolean enabled = true;
}
