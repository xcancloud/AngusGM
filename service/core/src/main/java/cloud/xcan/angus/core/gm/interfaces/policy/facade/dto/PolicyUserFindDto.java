package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Policy user find DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询角色用户请求参数")
public class PolicyUserFindDto extends PageQuery {

}
