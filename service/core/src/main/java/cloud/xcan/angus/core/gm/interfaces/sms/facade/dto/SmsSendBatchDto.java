package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "批量发送短信DTO")
public class SmsSendBatchDto {
    
    @NotEmpty
    @Schema(description = "手机号列表", required = true)
    private List<String> phones;
    
    @NotNull
    @Schema(description = "模板ID", required = true)
    private Long templateId;
    
    @Schema(description = "模板参数")
    private Map<String, String> params;
}
