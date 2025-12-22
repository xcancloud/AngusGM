package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>Service find DTO</p>
 */
@Data
@Schema(description = "服务查询DTO")
public class ServiceFindDto {

    @Schema(description = "搜索关键词（服务名称）")
    private String keyword;

    @Schema(description = "状态筛选（UP、DOWN、OUT_OF_SERVICE）")
    private String status;
}
