package cloud.xcan.angus.api.commonlink.setting.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.indicator.Perf;
import cloud.xcan.angus.spec.utils.JsonUtils;
import jakarta.persistence.AttributeConverter;

public class PerfDataConverter implements AttributeConverter<Perf, String> {

  @Override
  public String convertToDatabaseColumn(Perf attribute) {
    try {
      return JsonUtils.toJson(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public Perf convertToEntityAttribute(String dbData) {
    if (isEmpty(dbData)) {
      return null;
    }
    try {
      return JsonUtils.fromJson(dbData, Perf.class);
    } catch (Exception e) {
      return null;
    }
  }
}
