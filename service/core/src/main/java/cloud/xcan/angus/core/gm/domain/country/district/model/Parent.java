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
public class Parent extends ValueObjectSupport<Parent> {

  private long adcode;

  @Override
  public Parent copy() {
    return new Parent(this.adcode);
  }
}
