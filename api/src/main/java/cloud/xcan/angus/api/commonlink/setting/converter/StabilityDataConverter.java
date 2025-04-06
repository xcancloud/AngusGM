package cloud.xcan.angus.api.commonlink.setting.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.indicator.Stability;
import cloud.xcan.angus.spec.utils.JsonUtils;
import jakarta.persistence.AttributeConverter;

public class StabilityDataConverter implements AttributeConverter<Stability, String> {

  @Override
  public String convertToDatabaseColumn(Stability attribute) {
    try {
      return JsonUtils.toJson(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public Stability convertToEntityAttribute(String dbData) {
    if (isEmpty(dbData)) {
      return null;
    }
    try {
      return JsonUtils.fromJson(dbData, Stability.class);
    } catch (Exception e) {
      return null;
    }
  }
}
