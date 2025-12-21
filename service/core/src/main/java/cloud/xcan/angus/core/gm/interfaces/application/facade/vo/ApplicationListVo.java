package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationStatus;
import cloud.xcan.angus.core.gm.domain.application.enums.ApplicationType;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * Application List VO
 * </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "应用列表项")
public class ApplicationListVo extends TenantAuditingVo {

    @Schema(description = "应用ID")
    private Long id;

    @Schema(description = "应用编码")
    private String code;

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "应用类型")
    private ApplicationType type;

    @Schema(description = "应用版本")
    private String version;

    @Schema(description = "版本类型")
    private String versionType;

    @Schema(description = "是否默认应用")
    private Boolean isDefault;

    @Schema(description = "商店状态")
    private String shopStatus;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "状态")
    private ApplicationStatus status;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "应用URL")
    private String url;

    @Schema(description = "组ID")
    private String groupId;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否已安装")
    private Boolean isInstalled;
}
