package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class SystemVersionStatsVo implements Serializable {
    private Long total;
    private Long enabled;
    private Long disabled;
}
