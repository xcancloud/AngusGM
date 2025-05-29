package cloud.xcan.angus.api.commonlink.service;

import static cloud.xcan.angus.api.commonlink.AuthConstant.TOKEN_AUTH_RESOURCE_IGNORE_SUFFIX;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

/**
 * When ServiceResourceApi is interface and covert to dto:: Throw
 * java.lang.IllegalArgumentException: Projection type must be an interface!
 * <p>
 * Fix:: Use @Entity or @Value(@see AuthOrgPolicyP)
 */
@Entity
@Getter
public class ServiceResourceApi {

  private String resourceName;

  private String resourceDescription;

  @Id
  private Long apiId;

  private String apiName;

  private String apiCode;

  private Boolean apiEnabled;

  private String apiDescription;

  public boolean isIgnoredAuth() {
    if (isEmpty(resourceName)) {
      return true;
    }
    for (String ignoreSuffix : TOKEN_AUTH_RESOURCE_IGNORE_SUFFIX) {
      if (resourceName.endsWith(ignoreSuffix)) {
        return true;
      }
    }
    return false;
  }
}
