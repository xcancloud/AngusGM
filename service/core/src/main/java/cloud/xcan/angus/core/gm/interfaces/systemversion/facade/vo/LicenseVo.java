package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 许可证信息VO
 */
@Data
@Schema(description = "许可证信息")
public class LicenseVo {
    
    @Schema(description = "许可证类型", example = "企业版")
    private String licenseType;
    
    @Schema(description = "授权给", example = "联普云（北京）科技有限公司")
    private String licenseTo;
    
    @Schema(description = "许可证密钥", example = "ANGUS-GM-ENT-****-****-****")
    private String licenseKey;
    
    @Schema(description = "签发日期", example = "2024-01-01")
    private String issuedDate;
    
    @Schema(description = "过期日期", example = "2025-12-31")
    private String expiryDate;
    
    @Schema(description = "剩余天数", example = "12")
    private Integer remainingDays;
    
    @Schema(description = "状态", example = "有效")
    private String status;
    
    @Schema(description = "最大租户数", example = "100")
    private Integer maxTenants;
    
    @Schema(description = "最大用户数", example = "10000")
    private Integer maxUsers;
    
    @Schema(description = "功能列表")
    private List<String> features;
}
