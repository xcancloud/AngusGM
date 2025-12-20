package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import lombok.Data;

@Data
public class QuotaStatsVo {
    private Long totalCount;
    private Long exceededCount;
    private Long warningCount;
}
