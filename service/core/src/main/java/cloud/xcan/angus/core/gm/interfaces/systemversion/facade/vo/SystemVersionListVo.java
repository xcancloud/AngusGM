package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class SystemVersionListVo implements Serializable {
    private Long id;
    private String name;
    private Boolean enabled;
}
