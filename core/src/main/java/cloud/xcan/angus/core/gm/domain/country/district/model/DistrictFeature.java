package cloud.xcan.angus.core.gm.domain.country.district.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
public class DistrictFeature implements Serializable {

  private String id;

  private String type;

  @Type(JsonType.class)
  @Nullable
  private DistrictGeometry geometry;

  @Type(JsonType.class)
  @Nullable
  private DistrictProperties properties;

}
