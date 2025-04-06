package cloud.xcan.angus.core.gm.domain.country.district.model;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Features extends ValueObjectSupport<Features> {

  private String id;

  private String type;

  private Geometry geometry;

  private Properties properties;

  @Override
  public Features copy() {
    return new Features(this.id, this.type, this.geometry, this.properties);
  }
}
