package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "短信记录查询DTO")
public class SmsRecordFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "搜索关键词（手机号、内容）")
    private String keyword;
    
    @Schema(description = "状态筛选（成功、失败、待发送）")
    private String status;
    
    @Schema(description = "模板ID筛选")
    private Long templateId;
    
    @Schema(description = "开始日期")
    private LocalDateTime startDate;
    
    @Schema(description = "结束日期")
    private LocalDateTime endDate;
}
