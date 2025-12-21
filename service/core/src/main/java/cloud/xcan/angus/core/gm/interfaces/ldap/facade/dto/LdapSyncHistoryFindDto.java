package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "LDAP同步历史查询DTO")
public class LdapSyncHistoryFindDto implements Serializable {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "状态筛选（成功、失败、进行中）")
    private String status;
}
