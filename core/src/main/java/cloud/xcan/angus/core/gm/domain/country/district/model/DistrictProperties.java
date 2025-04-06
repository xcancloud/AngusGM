package cloud.xcan.angus.core.gm.domain.country.district.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
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
public class DistrictProperties {

  private String name;

  private String level;

  private Integer adcode;

  private List<Float> center;

  @Type(JsonType.class)
  private DistrictParent parent;

  @Type(JsonType.class)
  private List<Integer> acroutes;

  @Type(JsonType.class)
  private List<Float> centroid;

  private Integer childrenNum;

  private Integer subFeatureIndex;

}
