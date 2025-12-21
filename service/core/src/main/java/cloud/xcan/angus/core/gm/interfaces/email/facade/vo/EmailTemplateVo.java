package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmailTemplateVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String name;
    private String code;
    private String type;
    private String subject;
    private String content;
    private List<String> params;
    private String status;
    private Long usageCount;
    private Double openRate;
    private Double clickRate;
}
