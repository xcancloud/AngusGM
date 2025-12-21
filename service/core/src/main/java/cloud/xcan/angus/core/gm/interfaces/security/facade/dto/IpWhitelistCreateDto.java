package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加IP白名单DTO")
public class IpWhitelistCreateDto {
    
    @Schema(description = "IP地址", example = "192.168.2.50")
    private String ipAddress;
    
    @Schema(description = "IP范围", example = "192.168.2.1-192.168.2.100")
    private String ipRange;
    
    @Schema(description = "描述", example = "研发部网段")
    private String description;
    
    @Schema(description = "状态", example = "启用")
    private String status;
}
