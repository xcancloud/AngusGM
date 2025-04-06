package cloud.xcan.angus.core.gm.domain.country.district.model;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Geometry extends ValueObjectSupport<Geometry> {

  private String type;

  private List<List<List<Double[]>>> coordinates;

  @Override
  public Geometry copy() {
    return new Geometry(this.type, this.coordinates);
  }
}
