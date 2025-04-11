package cloud.xcan.angus.core.gm.domain.country;

import cloud.xcan.angus.core.gm.domain.country.district.model.GeoData;
import cloud.xcan.angus.spec.experimental.EntitySupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Entity
@Table(name = "c_country")
@Accessors(chain = true)
public class Country extends EntitySupport<Country, Long> {

  @Id
  private Long id;

  private String code;

  private String name;

  @Column(name = "chinese_name")
  private String chineseName;

  private String iso2;

  private String iso3;

  private String abbr;

  private Boolean open;

  @Transient
  private GeoData geo;

  @Override
  public Long identity() {
    return this.id;
  }
}
