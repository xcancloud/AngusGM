package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class SecurityStatsVo implements Serializable {
    private Long total;
    private Long enabled;
    private Long disabled;
}
