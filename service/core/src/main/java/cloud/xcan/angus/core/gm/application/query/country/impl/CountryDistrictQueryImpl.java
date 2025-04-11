package cloud.xcan.angus.core.gm.application.query.country.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.converter.DistrictConverter;
import cloud.xcan.angus.core.gm.application.query.country.CountryDistrictQuery;
import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.domain.country.district.DistrictRepo;
import cloud.xcan.angus.core.gm.domain.country.district.model.DistrictLevel;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictTreeVo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Biz
public class CountryDistrictQueryImpl implements CountryDistrictQuery {

  @Resource
  private DistrictRepo districtRepo;

  @Override
  public District detail(String countryCode, String districtCode) {
    return new BizTemplate<District>() {

      @Override
      protected District process() {
        return districtRepo.findByCountryCodeAndCode(countryCode, districtCode);
      }
    }.execute();
  }

  @Override
  public List<District> province(String countryCode) {
    return new BizTemplate<List<District>>() {

      @Override
      protected List<District> process() {
        return districtRepo.findByCountryCodeAndLevel(countryCode, DistrictLevel.ONE);
      }
    }.execute();
  }

  @Override
  public List<District> city(String countryCode, String provinceCode) {
    return new BizTemplate<List<District>>() {

      @Override
      protected List<District> process() {
        return new ArrayList<>(
            districtRepo.findByCountryCodeAndParentCodeAndLevel(countryCode, provinceCode,
                DistrictLevel.TWO));
      }
    }.execute();
  }

  @Override
  public List<District> areas(String countryCode, String cityCode) {
    return new BizTemplate<List<District>>() {

      @Override
      protected List<District> process() {
        return new ArrayList<>(
            districtRepo.findByCountryCodeAndParentCodeAndLevel(countryCode, cityCode,
                DistrictLevel.Three));
      }
    }.execute();
  }

  @Override
  public List<CountryDistrictTreeVo> tree(String countryCode, String parentCode) {
    return new BizTemplate<List<CountryDistrictTreeVo>>() {

      @Override
      protected List<CountryDistrictTreeVo> process() {
        List<District> districts = new ArrayList<>(districtRepo.findByCountryCode(countryCode));
        return makeTree(districts.stream().map(DistrictConverter::toDistrictTreeVo)
            .collect(Collectors.toList()), parentCode);
      }
    }.execute();
  }

  @Override
  public District find(Long id) {
    return districtRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "District"));
  }

  private List<CountryDistrictTreeVo> makeTree(List<CountryDistrictTreeVo> districts,
      String parentCode) {
    List<CountryDistrictTreeVo> children = districts.stream()
        .filter(x -> x.getParentCode().equals(parentCode)).collect(Collectors.toList());
    List<CountryDistrictTreeVo> successor = districts.stream()
        .filter(x -> !x.getParentCode().equals(parentCode)).collect(Collectors.toList());
    children.forEach(x -> {
          makeTree(successor, x.getCode()).forEach(
              y -> x.getChildren().add(y)
          );
        }
    );
    return children;
  }

}
