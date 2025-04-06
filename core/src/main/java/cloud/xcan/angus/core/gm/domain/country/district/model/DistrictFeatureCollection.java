package cloud.xcan.angus.core.gm.domain.country.district.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
public class DistrictFeatureCollection implements Serializable {

  private String type;

  @Type(JsonType.class)
  private List<DistrictFeature> features;

}
