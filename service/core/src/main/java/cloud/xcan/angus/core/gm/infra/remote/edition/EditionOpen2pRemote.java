package cloud.xcan.angus.core.gm.infra.remote.edition;

import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "XCAN-ANGUSESS.BOOT", url = "${xcan.cloud.storeApiUrlPrefix:undefined}")
public interface EditionOpen2pRemote {

  @Operation(summary = "Query the latest and authorized edition of product..", operationId = "edition:latest:detail:openapi2p")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/openapi2p/v1/edition/latest")
  ApiLocaleResult<List<LatestEditionVo>> latest(
      @Parameter(name = "goodsCode", description = "Goods code", required = true)
      @RequestParam(value = "goodsCode", required = true) String goodsCode,
      @Parameter(name = "goodsId", description = "Goods id", required = false)
      @RequestParam(value = "goodsId", required = false) Long goodsId);
}
