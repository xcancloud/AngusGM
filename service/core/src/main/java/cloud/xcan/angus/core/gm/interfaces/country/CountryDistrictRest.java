package cloud.xcan.angus.core.gm.interfaces.country;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import cloud.xcan.angus.core.gm.interfaces.country.facade.CountryDistrictFacade;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryDistrictFindDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Country District", description = "REST API endpoints for querying supported country districts and administrative regions")
@Validated
@RestController
@RequestMapping("/api/v1/country")
public class CountryDistrictRest {

  @Resource
  private CountryDistrictFacade districtFacade;

  @Operation(summary = "Retrieve detailed district information", operationId = "country:district:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "District details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "District not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{countryCode}/district/{districtCode}")
  public ApiLocaleResult<CountryDistrictDetailVo> detail(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", description = "Country code", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("districtCode") @Parameter(name = "districtCode", description = "District code", required = true) String districtCode) {
    return ApiLocaleResult.success(districtFacade.district(countryCode, districtCode));
  }

  @Operation(summary = "Retrieve province list for country", operationId = "country:province:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Province list retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Country not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{countryCode}/province")
  public ApiLocaleResult<List<CountryDistrictDetailVo>> province(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", description = "Country code", required = true) String countryCode) {
    return ApiLocaleResult.success(districtFacade.province(countryCode));
  }

  @Operation(summary = "Retrieve city list for province", operationId = "country:city:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "City list retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Province not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{countryCode}/province/{provinceCode}/city")
  public ApiLocaleResult<List<CountryDistrictDetailVo>> city(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", description = "Country code", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("provinceCode") @Parameter(name = "provinceCode", description = "Province code", required = true) String provinceCode) {
    return ApiLocaleResult.success(districtFacade.city(countryCode, provinceCode));
  }

  @Operation(summary = "Retrieve area list for city", operationId = "country:area:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Area list retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "City not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{countryCode}/city/{cityCode}/area")
  public ApiLocaleResult<List<CountryDistrictDetailVo>> areas(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", description = "Country code", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("cityCode") @Parameter(name = "cityCode", description = "City code", required = true) String cityCode) {
    return ApiLocaleResult.success(districtFacade.areas(countryCode, cityCode));
  }

  @Operation(summary = "Retrieve district list with filtering and pagination", operationId = "country:district:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "District list retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/district")
  public ApiLocaleResult<PageResult<CountryDistrictDetailVo>> list(
      @Valid CountryDistrictFindDto dto) {
    return ApiLocaleResult.success(districtFacade.list(dto));
  }

  @Operation(summary = "Retrieve district tree structure", operationId = "country:district:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "District tree retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "District not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{countryCode}/district/{districtCode}/tree")
  public ApiLocaleResult<List<CountryDistrictTreeVo>> tree(
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("countryCode") @Parameter(name = "countryCode", description = "Country code", required = true) String countryCode,
      @Valid @Length(max = MAX_CODE_LENGTH) @PathVariable("districtCode") @Parameter(name = "districtCode", description = "District code", required = true) String districtCode) {
    return ApiLocaleResult.success(districtFacade.tree(countryCode, districtCode));
  }
}
