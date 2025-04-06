package cloud.xcan.angus.core.gm.interfaces.country;

import cloud.xcan.angus.core.gm.interfaces.country.facade.CountryFacade;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountryFindDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.dto.CountrySearchDto;
import cloud.xcan.angus.core.gm.interfaces.country.facade.vo.CountryDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Country")
@Validated
@RestController
@RequestMapping("/api/v1/country")
public class CountryRest {

  @Resource
  private CountryFacade countryFacade;

  @Operation(description = "Query the detail of country", operationId = "country:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<CountryDetailVo> detail(
      @Parameter(name = "id", description = "Country ID", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(countryFacade.detail(id));
  }

  @Operation(description = "Query the list of country", operationId = "country:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<CountryDetailVo>> list(@Valid CountryFindDto dto) {
    return ApiLocaleResult.success(countryFacade.list(dto));
  }

  @Operation(description = "Fulltext search the list of country", operationId = "country:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<CountryDetailVo>> search(@Valid CountrySearchDto dto) {
    return ApiLocaleResult.success(countryFacade.search(dto));
  }
}
