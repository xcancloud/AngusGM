package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "邮件模板查询DTO")
public class EmailTemplateFindDto extends PageQuery {
    
    @Schema(description = "搜索关键词")
    private String keyword;
    
    @Schema(description = "状态筛选（已启用、已禁用）")
    private String status;
    
    @Schema(description = "类型筛选（系统邮件、营销邮件、通知邮件）")
    private String type;
}
