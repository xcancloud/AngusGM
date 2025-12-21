package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 许可证更新DTO
 */
@Data
@Schema(description = "许可证更新请求")
public class LicenseUpdateDto {
    
    @Schema(description = "许可证密钥", example = "ANGUS-GM-ENT-XXXX-XXXX-XXXX-XXXX", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "许可证密钥不能为空")
    private String licenseKey;
}
