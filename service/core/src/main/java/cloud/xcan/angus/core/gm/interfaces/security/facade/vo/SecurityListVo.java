package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class SecurityListVo implements Serializable {
    private Long id;
    private String name;
    private Boolean enabled;
}
