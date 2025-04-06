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
public class GeoData extends ValueObjectSupport<GeoData> {

  private String type;

  private List<Features> features;

  @Override
  public GeoData copy() {
    return new GeoData(this.type, this.features);
  }
}
