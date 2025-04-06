package cloud.xcan.angus.api.commonlink.setting.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.security.Security;
import cloud.xcan.angus.core.utils.GsonUtils;
import jakarta.persistence.AttributeConverter;

public class SecurityDataConverter implements AttributeConverter<Security, String> {

  @Override
  public String convertToDatabaseColumn(Security attribute) {
    try {
      return GsonUtils.toJson(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public Security convertToEntityAttribute(String dbData) {
    if (isEmpty(dbData)) {
      return null;
    }
    try {
      return GsonUtils.fromJson(dbData, Security.class);
    } catch (Exception e) {
      return null;
    }
  }
}
