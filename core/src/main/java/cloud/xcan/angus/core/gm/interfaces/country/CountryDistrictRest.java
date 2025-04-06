package cloud.xcan.angus.core.gm.interfaces.country;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import cloud.xcan.angus.core.gm.interfaces.country.facade.CountryDistrictFacade;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryDistrictSearchDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictDetailVo;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDistrictTreeVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CountryDistrict")
@Validated
@RestController
@RequestMapping("/api/v1/country")
public class CountryDistrictRest {

  @Resource
  private CountryDistrictFacade districtFacade;

  @Operation(description = "Query the detail of district", operationId = "country:district:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping(value = "/{countryCode}/district/{districtCode}")
  public ApiLocaleResult<CountryDistrictDetailVo> detail(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("districtCode") @Parameter(name = "districtCode", required = true) String districtCode) {
    return ApiLocaleResult.success(districtFacade.district(countryCode, districtCode));
  }

  @Operation(description = "Query the province list of country", operationId = "country:province:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping("/{countryCode}/province")
  public ApiLocaleResult<List<CountryDistrictDetailVo>> province(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", required = true) String countryCode) {
    return ApiLocaleResult.success(districtFacade.province(countryCode));
  }

  @Operation(description = "Query the city list of province", operationId = "country:city:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping("/{countryCode}/province/{provinceCode}/city")
  public ApiLocaleResult<List<CountryDistrictDetailVo>> city(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("provinceCode") @Parameter(name = "provinceCode", required = true) String provinceCode) {
    return ApiLocaleResult.success(districtFacade.city(countryCode, provinceCode));
  }

  @Operation(description = "Query the area list of city", operationId = "country:area:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping("/{countryCode}/city/{cityCode}/area")
  public ApiLocaleResult<List<CountryDistrictDetailVo>> areas(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("cityCode") @Parameter(name = "cityCode", required = true) String cityCode) {
    return ApiLocaleResult.success(districtFacade.areas(countryCode, cityCode));
  }

  @Operation(description = "Fulltext search the list of district", operationId = "country:district:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/district/search")
  public ApiLocaleResult<PageResult<CountryDistrictDetailVo>> search(
      @Valid CountryDistrictSearchDto dto) {
    return ApiLocaleResult.success(districtFacade.search(dto));
  }

  @Operation(description = "Query the district tree of country", operationId = "country:district:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping("/{countryCode}/district/{districtCode}/tree")
  public ApiLocaleResult<List<CountryDistrictTreeVo>> tree(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("districtCode") @Parameter(name = "districtCode", required = true) String districtCode) {
    return ApiLocaleResult.success(districtFacade.tree(countryCode, districtCode));
  }
}
