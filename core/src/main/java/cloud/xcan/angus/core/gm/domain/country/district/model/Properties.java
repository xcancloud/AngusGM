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
public class Properties extends ValueObjectSupport<Properties> {

  private String name;
  private String level;
  private long adcode;
  private List<Double> center;
  private Parent parent;
  private List<Long> acroutes;
  private List<Double> centroid;
  private int childrenNum;
  private int subFeatureIndex;

  @Override
  public Properties copy() {
    return new Properties(this.name, this.level, this.adcode, this.center, this.parent,
        this.acroutes, this.centroid, this.childrenNum, this.subFeatureIndex);
  }
}
