package cloud.xcan.angus.api.commonlink.setting.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.locale.Locale;
import cloud.xcan.angus.core.utils.GsonUtils;
import jakarta.persistence.AttributeConverter;

public class LocaleDataConverter implements AttributeConverter<Locale, String> {

  @Override
  public String convertToDatabaseColumn(Locale attribute) {
    try {
      return GsonUtils.toJson(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public Locale convertToEntityAttribute(String dbData) {
    if (isEmpty(dbData)) {
      return null;
    }
    try {
      return GsonUtils.fromJson(dbData, Locale.class);
    } catch (Exception e) {
      return null;
    }
  }
}
