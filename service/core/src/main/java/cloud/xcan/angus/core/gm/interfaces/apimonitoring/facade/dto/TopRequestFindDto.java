package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "TOP请求查询DTO")
public class TopRequestFindDto implements Serializable {
    
    @Schema(description = "返回数量", example = "10")
    private Integer limit = 10;
    
    @Schema(description = "时间周期（1h、6h、24h、7d、30d）")
    private String period;
}
