package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.enums.ResourceAclType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class AuthorizedResourceDto {

  @NotEmpty
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Authorized resource identifier name", requiredMode = RequiredMode.REQUIRED)
  private String resource;

  @Schema(description = "Resource identifiers for authorized resources (required when authType=API)")
  private LinkedHashSet<Long> resourceIds;

  @Schema(description = "Access control list types for authorized resources (required when authType=ACL)")
  private Map<Long, List<ResourceAclType>> resourceAcls;

}
