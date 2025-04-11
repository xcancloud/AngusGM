package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.spec.experimental.ValueObjectSupport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address extends ValueObjectSupport<Address> {

  private String provinceCode;

  private String provinceName;

  private String cityCode;

  private String cityName;

  private String street;

  @Override
  public Address copy() {
    return new Address(this.provinceCode, this.provinceName, this.cityCode, this.cityName,
        this.street);
  }
}
