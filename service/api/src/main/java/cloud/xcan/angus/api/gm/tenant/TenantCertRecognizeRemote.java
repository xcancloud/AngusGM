package cloud.xcan.angus.api.gm.tenant;

import cloud.xcan.angus.api.gm.tenant.dto.cert.BusinessRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.dto.cert.IdCardRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.vo.cert.BusinessRecognizeVo;
import cloud.xcan.angus.api.gm.tenant.vo.cert.IdCardRecognizeVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface TenantCertRecognizeRemote {

  @Operation(summary = "Recognize business license.", operationId = "cert:business:recognize")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully recognize")})
  @GetMapping("/api/v1/cert/business/recognize")
  ApiLocaleResult<BusinessRecognizeVo> businessRecognize(
      @Valid @SpringQueryMap BusinessRecognizeDto dto);

  @Operation(summary = "Recognize personal ID card.", operationId = "cert:idcard:recognize")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully recognize")})
  @GetMapping("/api/v1/cert/idcard/recognize")
  ApiLocaleResult<IdCardRecognizeVo> idcardRecognize(@Valid @SpringQueryMap IdCardRecognizeDto dto);

}
