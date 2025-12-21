package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "应用模板DTO")
public class ApplyTemplateDto implements Serializable {
    
    @NotBlank
    @Schema(description = "模板ID")
    private String templateId;
}
