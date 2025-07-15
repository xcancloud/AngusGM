package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.enums.ResourceAclType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import java.util.LinkedHashSet;
import java.util.List;
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
  @Schema(description = "Authorized resource name", requiredMode = RequiredMode.REQUIRED)
  private String resource;

  @Schema(description = "Api ids of authorized resources, when authType=API is required")
  private LinkedHashSet<Long> apiIds;

  @Schema(description = "ACL types of authorized resources, when authType=ACL is required")
  private List<ResourceAclType> acls;

}
