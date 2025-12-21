package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "短信模板查询DTO")
public class SmsTemplateFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "搜索关键词")
    private String keyword;
    
    @Schema(description = "状态筛选（已启用、已禁用）")
    private String status;
    
    @Schema(description = "类型筛选（验证码、通知、营销）")
    private String type;
}
