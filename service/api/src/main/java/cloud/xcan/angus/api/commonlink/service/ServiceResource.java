package cloud.xcan.angus.api.commonlink.service;

import static cloud.xcan.angus.api.commonlink.AASConstant.TOKEN_AUTH_RESOURCE_IGNORE_SUFFIX;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import jakarta.persistence.Transient;

public interface ServiceResource {

  String getServiceCode();

  String getResourceName();

  String getResourceDesc();

  @Transient
  default boolean isIgnoredAuth() {
    if (isEmpty(getResourceName())) {
      return true;
    }
    for (String ignoreSuffix : TOKEN_AUTH_RESOURCE_IGNORE_SUFFIX) {
      if (getResourceName().endsWith(ignoreSuffix)) {
        return true;
      }
    }
    return false;
  }

}
