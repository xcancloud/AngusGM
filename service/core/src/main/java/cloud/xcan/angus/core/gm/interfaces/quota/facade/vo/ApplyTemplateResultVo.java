package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "应用模板结果VO")
public class ApplyTemplateResultVo implements Serializable {
    
    @Schema(description = "租户ID")
    private String tenantId;
    
    @Schema(description = "模板ID")
    private String templateId;
    
    @Schema(description = "模板名称")
    private String templateName;
    
    @Schema(description = "应用时间")
    private LocalDateTime appliedDate;
}
