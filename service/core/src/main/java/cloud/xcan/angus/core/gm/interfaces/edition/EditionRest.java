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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Edition")
@Validated
@RestController
@RequestMapping("/api/v1/edition")
public class EditionRest {

  @Resource
  private EditionFacade editionFacade;

  @Operation(summary = "Query the installed and authorized edition of product.", operationId = "edition:installed:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/installed")
  public ApiLocaleResult<InstalledEditionVo> installed(
      @Parameter(name = "goodsCode", description = "Goods code", required = true) @RequestParam("goodsCode") String goodsCode) {
    return ApiLocaleResult.success(editionFacade.installed(goodsCode));
  }

  @Operation(summary = "Query the latest and upgradeable edition of product.", operationId = "edition:upgradeable:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/upgradeable")
  public ApiLocaleResult<List<LatestEditionVo>> upgradeable(
      @RequestParam("goodsCode")
      @Parameter(name = "goodsCode", description = "Goods code", required = true) String goodsCode,
      @RequestParam(value = "goodsId", required = false)
      @Parameter(name = "goodsId", description = "Goods id", required = false) Long goodsId) {
    return ApiLocaleResult.success(editionFacade.upgradeable(goodsCode, goodsId));
  }
}
