package cloud.xcan.angus.core.gm.domain.country.district.model;

import cloud.xcan.angus.spec.ValueObject;
import lombok.Getter;


@Getter
public enum DistrictLevel implements ValueObject<DistrictLevel> {
  ZERO(0),
  ONE(1),
  TWO(2),
  Three(3);

  private final Integer value;

  DistrictLevel(Integer value) {
    this.value = value;
  }

  @Override
  public boolean sameValueAs(DistrictLevel level) {
    return this.getValue().equals(level.getValue());
  }

}
