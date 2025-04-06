package cloud.xcan.angus.core.gm.domain.country.district;


import cloud.xcan.angus.core.gm.domain.country.district.model.DistrictLevel;
import cloud.xcan.angus.core.gm.domain.country.district.model.GeoData;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "c_district")
@Accessors(chain = true)
public class District extends EntitySupport<District, Long> {

  @Id
  private Long id;

  private String code;

  private String name;

  @Column(name = "pin_yin")
  private String pinYin;

  @Column(name = "parent_code")
  private String parentCode;

  @Column(name = "simple_name")
  private String simpleName;

  @Enumerated(EnumType.ORDINAL)
  private DistrictLevel level;

  @Column(name = "country_code")
  private String countryCode;

  @Column(name = "country_name")
  private String countryName;

  @Column(name = "city_code")
  private String cityCode;

  @Column(name = "zip_code")
  private String zipCode;

  @Column(name = "mer_name")
  private String merName;

  private Float lng;

  private Float lat;

  /**
   * The data format is too low and initialization is too slow. Remove data temporarily.
   */
  @Transient
  private GeoData geo;

  @Override
  public Long identity() {
    return this.id;
  }

}
