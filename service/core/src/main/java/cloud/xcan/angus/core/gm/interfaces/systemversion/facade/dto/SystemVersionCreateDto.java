package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SystemVersionCreateDto implements Serializable {
    private String name;
    private String description;
    private Boolean enabled = true;
}
