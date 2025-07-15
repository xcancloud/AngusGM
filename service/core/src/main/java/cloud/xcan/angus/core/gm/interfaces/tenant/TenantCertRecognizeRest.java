package cloud.xcan.angus.core.gm.interfaces.tenant;

import cloud.xcan.angus.api.gm.tenant.dto.cert.BusinessRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.dto.cert.IdCardRecognizeDto;
import cloud.xcan.angus.api.gm.tenant.vo.cert.BusinessRecognizeVo;
import cloud.xcan.angus.api.gm.tenant.vo.cert.IdCardRecognizeVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantCertRecognizeFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TenantCertRecognize", description = "Validates official IDs or business licenses via third-party APIs for automated authentication")
@Validated
@RestController
@RequestMapping("/api/v1/cert")
public class TenantCertRecognizeRest {

  @Resource
  private TenantCertRecognizeFacade certRecognizeFacade;

  @Operation(summary = "Recognize business license", operationId = "cert:business:recognize")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully recognize")})
  @GetMapping("/business/recognize")
  public ApiLocaleResult<BusinessRecognizeVo> businessRecognize(@Valid BusinessRecognizeDto dto) {
    return ApiLocaleResult.success(certRecognizeFacade.businessRecognize(dto));
  }

  @Operation(summary = "Recognize ID card", operationId = "cert:idcard:recognize")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully recognize")})
  @GetMapping("/idcard/recognize")
  public ApiLocaleResult<IdCardRecognizeVo> idcardRecognize(@Valid IdCardRecognizeDto dto) {
    return ApiLocaleResult.success(certRecognizeFacade.idcardRecognize(dto));
  }

}
