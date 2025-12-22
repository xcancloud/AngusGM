package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "邮件模板VO")
public class EmailTemplateVo extends TenantAuditingVo {
    
    @Schema(description = "模板ID")
    private Long id;
    
    @Schema(description = "模板名称")
    private String name;
    
    @Schema(description = "模板编码")
    private String code;
    
    @Schema(description = "模板类型")
    private String type;
    
    @Schema(description = "邮件主题")
    private String subject;
    
    @Schema(description = "模板内容")
    private String content;
    
    @Schema(description = "模板参数列表")
    private List<String> params;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "使用次数")
    private Long usageCount;
    
    @Schema(description = "打开率")
    private Double openRate;
    
    @Schema(description = "点击率")
    private Double clickRate;
}
