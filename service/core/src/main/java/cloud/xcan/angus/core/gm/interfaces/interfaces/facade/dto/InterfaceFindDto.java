package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto;

import cloud.xcan.angus.core.gm.domain.interfaces.enums.HttpMethod;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceStatus;
import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Interface find DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询接口请求参数")
public class InterfaceFindDto extends PageQuery {

  @Schema(description = "服务名称筛选")
  private String serviceName;

  @Schema(description = "搜索关键词（接口路径、描述）")
  private String keyword;

  @Schema(description = "请求方法筛选（GET、POST、PUT、DELETE、PATCH）")
  private HttpMethod method;

  @Schema(description = "标签筛选")
  private List<String> tags;

  @Schema(description = "是否已废弃")
  private Boolean deprecated;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "path", "name"})
  private String orderBy = "createdDate";
}
