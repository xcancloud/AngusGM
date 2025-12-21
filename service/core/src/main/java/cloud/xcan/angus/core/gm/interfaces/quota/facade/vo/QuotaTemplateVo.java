package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "配额模板VO")
public class QuotaTemplateVo implements Serializable {
    
    @Schema(description = "模板ID")
    private String id;
    
    @Schema(description = "模板名称")
    private String name;
    
    @Schema(description = "模板描述")
    private String description;
    
    @Schema(description = "用户配额")
    private Integer users;
    
    @Schema(description = "存储配额")
    private String storage;
    
    @Schema(description = "应用配额")
    private Integer applications;
    
    @Schema(description = "API调用配额")
    private Long apiCalls;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "是否默认")
    private Boolean isDefault;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
    
    @Schema(description = "修改时间")
    private LocalDateTime modifiedDate;
}
