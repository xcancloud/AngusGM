package cloud.xcan.angus.core.gm.domain.country.district.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
public class Feature implements Serializable {

  private String id;

  private String type;

  private Geometry geometry;

  private Property property;
}
