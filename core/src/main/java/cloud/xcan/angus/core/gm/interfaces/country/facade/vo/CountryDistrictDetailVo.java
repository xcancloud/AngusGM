package cloud.xcan.angus.core.gm.interfaces.country.facade.vo;

import cloud.xcan.angus.core.gm.domain.country.district.model.DistrictLevel;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CountryDistrictDetailVo implements Serializable {

  private Long id;

  private String code;

  private String name;

  private String pinYin;

  private String parentCode;

  private String simpleName;

  private DistrictLevel level;

  private String countryCode;

  private String countryName;

  private String cityCode;

  private String zipCode;

  private String merName;

  private Float lng;

  private Float lat;

}
