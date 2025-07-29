package cloud.xcan.angus.core.gm.interfaces.edition;

import cloud.xcan.angus.core.gm.infra.remote.edition.InstalledEditionVo;
import cloud.xcan.angus.core.gm.infra.remote.edition.LatestEditionVo;
import cloud.xcan.angus.core.gm.interfaces.edition.facade.EditionFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Edition", description = "REST API endpoints for managing product editions including installed versions and upgradeable options")
@Validated
@RestController
@RequestMapping("/api/v1/edition")
public class EditionRest {

  @Resource
  private EditionFacade editionFacade;

  @Operation(summary = "Retrieve installed and authorized product edition information", operationId = "edition:installed:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Installed edition information retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/installed")
  public ApiLocaleResult<InstalledEditionVo> installed(
      @Parameter(name = "goodsCode", description = "Product goods code", required = true) @RequestParam("goodsCode") String goodsCode) {
    return ApiLocaleResult.success(editionFacade.installed(goodsCode));
  }

  @Operation(summary = "Retrieve latest upgradeable product edition information", operationId = "edition:upgradeable:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Upgradeable edition information retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/upgradeable")
  public ApiLocaleResult<List<LatestEditionVo>> upgradeable(
      @RequestParam("goodsCode")
      @Parameter(name = "goodsCode", description = "Product goods code", required = true) String goodsCode,
      @RequestParam(value = "goodsId", required = false)
      @Parameter(name = "goodsId", description = "Product goods identifier", required = false) Long goodsId) {
    return ApiLocaleResult.success(editionFacade.upgradeable(goodsCode, goodsId));
  }
}
