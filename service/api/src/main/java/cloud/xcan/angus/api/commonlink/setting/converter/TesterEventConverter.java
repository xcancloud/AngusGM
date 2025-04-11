package cloud.xcan.angus.api.commonlink.setting.converter;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.core.utils.GsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import java.util.List;

public class TesterEventConverter implements AttributeConverter<List<TesterEvent>, String> {

  @Override
  public String convertToDatabaseColumn(List<TesterEvent> attribute) {
    try {
      return GsonUtils.toJson(attribute);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public List<TesterEvent> convertToEntityAttribute(String dbData) {
    if (isEmpty(dbData)) {
      return null;
    }
    try {
      return GsonUtils.fromJson(dbData, new TypeReference<List<TesterEvent>>() {
      }.getType());
    } catch (Exception e) {
      return null;
    }
  }
}
