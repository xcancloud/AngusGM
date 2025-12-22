package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import cloud.xcan.angus.core.gm.domain.interfaces.enums.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * Interface list VO
 */
@Data
@Schema(description = "接口列表项")
public class InterfaceListVo {

  @Schema(description = "接口ID")
  private Long id;

  @Schema(description = "服务名称")
  private String serviceName;

  @Schema(description = "接口编码")
  private String code;

  @Schema(description = "接口路径")
  private String path;

  @Schema(description = "请求方法")
  private HttpMethod method;

  @Schema(description = "接口摘要")
  private String summary;

  @Schema(description = "接口描述")
  private String description;

  @Schema(description = "标签列表")
  private List<String> tags;

  @Schema(description = "是否已废弃")
  private Boolean deprecated;

  @Schema(description = "版本号")
  private String version;
}
