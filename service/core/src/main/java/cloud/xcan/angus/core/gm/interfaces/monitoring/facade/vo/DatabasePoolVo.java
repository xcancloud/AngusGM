package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

@Data
public class DatabasePoolVo {
    private String name;
    private String database;
    private Integer active;
    private Integer idle;
    private Integer total;
    private Integer maxPool;
    private Integer waiting;
    private String status;
}
