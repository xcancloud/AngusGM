package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "安全事件查询DTO")
public class SecurityEventFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "事件类型（登录失败、密码修改、权限变更）")
    private String type;
    
    @Schema(description = "风险等级（低、中、高）")
    private String level;
    
    @Schema(description = "用户ID筛选")
    private Long userId;
    
    @Schema(description = "开始日期")
    private LocalDateTime startDate;
    
    @Schema(description = "结束日期")
    private LocalDateTime endDate;
}
