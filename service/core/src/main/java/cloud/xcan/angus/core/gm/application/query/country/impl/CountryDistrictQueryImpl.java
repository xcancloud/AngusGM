package cloud.xcan.angus.core.gm.application.query.country.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.converter.DistrictConverter;
import cloud.xcan.angus.core.gm.application.query.country.CountryDistrictQuery;
import cloud.xcan.angus.core.gm.domain.country.district.District;
import cloud.xcan.angus.core.gm.domain.country.district.DistrictRepo;
import cloud.xcan.angus.core.gm.domain.country.district.DistrictSearchRepo;
import cloud.xcan.angus.core.gm.domain.country.district.model.DistrictLevel;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictTreeVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of country district query operations.
 * </p>
 * <p>
 * Manages country district retrieval, hierarchical queries, and tree structure generation.
 * Provides comprehensive district querying with full-text search support.
 * </p>
 * <p>
 * Supports district detail retrieval, hierarchical queries (province, city, area),
 * tree structure generation, and paginated listing for country district management.
 * </p>
 */
@Biz
public class CountryDistrictQueryImpl implements CountryDistrictQuery {

  @Resource
  private DistrictRepo districtRepo;
  @Resource
  private DistrictSearchRepo districtSearchRepo;

  /**
   * <p>
   * Retrieves detailed district information by country and district codes.
   * </p>
   * <p>
   * Fetches complete district record with all associated information.
   * </p>
   */
  @Override
  public District detail(String countryCode, String districtCode) {
    return new BizTemplate<District>() {

      @Override
      protected District process() {
        return districtRepo.findByCountryCodeAndCode(countryCode, districtCode);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves districts with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering.
   * Returns paginated results for comprehensive district management.
   * </p>
   */
  @Override
  public Page<District> list(GenericSpecification<District> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<District>>() {
      @Override
      protected Page<District> process() {
        return fullTextSearch
            ? districtSearchRepo.find(spec.getCriteria(), pageable, District.class, match)
            : districtRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves provinces for the specified country.
   * </p>
   * <p>
   * Returns all first-level districts (provinces) for the given country code.
   * </p>
   */
  @Override
  public List<District> province(String countryCode) {
    return new BizTemplate<List<District>>() {

      @Override
      protected List<District> process() {
        return districtRepo.findByCountryCodeAndLevel(countryCode, DistrictLevel.ONE);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves cities for the specified province.
   * </p>
   * <p>
   * Returns all second-level districts (cities) under the given province code.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves areas for the specified city.
   * </p>
   * <p>
   * Returns all third-level districts (areas) under the given city code.
   * </p>
   */
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

  /**
   * <p>
   * Generates hierarchical tree structure for country districts.
   * </p>
   * <p>
   * Creates tree structure starting from the specified parent code.
   * Converts districts to tree view objects for hierarchical display.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves district by ID with validation.
   * </p>
   * <p>
   * Verifies district exists and returns district information.
   * Throws ResourceNotFound exception if district does not exist.
   * </p>
   */
  @Override
  public District find(Long id) {
    return districtRepo.findById(id)
        .orElseThrow(() -> ResourceNotFound.of(id, "District"));
  }

  /**
   * <p>
   * Builds hierarchical tree structure from district list.
   * </p>
   * <p>
   * Recursively constructs tree structure starting from specified parent code.
   * Filters districts by parent code and builds parent-child relationships.
   * </p>
   */
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
