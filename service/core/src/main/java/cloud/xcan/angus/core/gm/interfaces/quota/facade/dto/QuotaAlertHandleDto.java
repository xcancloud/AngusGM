package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "配额告警处理DTO")
public class QuotaAlertHandleDto implements Serializable {
    
    @NotBlank
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "处理备注")
    private String handleNote;
}
