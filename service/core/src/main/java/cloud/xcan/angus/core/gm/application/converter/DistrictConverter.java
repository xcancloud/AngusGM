package cloud.xcan.angus.core.gm.application.converter;

import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictTreeVo;

public class DistrictConverter {

  public static CountryDistrictTreeVo toDistrictTreeVo(District district) {
    CountryDistrictTreeVo tree = new CountryDistrictTreeVo();
    tree.setCode(district.getCode()).setCityCode(district.getCityCode())
        .setCountryCode(district.getCountryCode())
        .setCountryName(district.getCountryName())
        .setId(district.getId())
        .setLat(district.getLat())
        .setLevel(district.getLevel())
        .setLng(district.getLng())
        .setMerName(district.getMerName())
        .setParentCode(district.getParentCode())
        .setName(district.getName())
        .setPinYin(district.getPinYin())
        .setSimpleName(district.getSimpleName())
        .setZipCode(district.getZipCode());
    return tree;
  }

}
