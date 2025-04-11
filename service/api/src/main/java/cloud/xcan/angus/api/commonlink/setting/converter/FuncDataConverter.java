package cloud.xcan.angus.api.commonlink.setting.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.indicator.Func;
import cloud.xcan.angus.spec.utils.JsonUtils;
import jakarta.persistence.AttributeConverter;

public class FuncDataConverter implements AttributeConverter<Func, String> {

  @Override
  public String convertToDatabaseColumn(Func attribute) {
    try {
      return JsonUtils.toJson(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public Func convertToEntityAttribute(String dbData) {
    if (isEmpty(dbData)) {
      return null;
    }
    try {
      return JsonUtils.fromJson(dbData, Func.class);
    } catch (Exception e) {
      return null;
    }
  }
}
