package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SystemVersionFindDto implements Serializable {
    private String name;
    private Boolean enabled;
}
