package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "配额模板创建DTO")
public class QuotaTemplateCreateDto implements Serializable {
    
    @NotBlank
    @Schema(description = "模板名称")
    private String name;
    
    @Schema(description = "模板描述")
    private String description;
    
    @NotNull
    @Schema(description = "用户配额")
    private Integer users;
    
    @NotBlank
    @Schema(description = "存储配额")
    private String storage;
    
    @NotNull
    @Schema(description = "应用配额")
    private Integer applications;
    
    @NotNull
    @Schema(description = "API调用配额")
    private Long apiCalls;
    
    @Schema(description = "价格")
    private BigDecimal price;
}
