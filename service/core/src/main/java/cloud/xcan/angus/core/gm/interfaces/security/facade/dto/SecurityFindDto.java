package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SecurityFindDto implements Serializable {
    private String name;
    private Boolean enabled;
}
