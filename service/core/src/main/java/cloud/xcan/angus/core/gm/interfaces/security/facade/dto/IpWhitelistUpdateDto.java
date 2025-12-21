package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新IP白名单DTO")
public class IpWhitelistUpdateDto {
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "IP范围")
    private String ipRange;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "状态")
    private String status;
}
