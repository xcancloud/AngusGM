package cloud.xcan.angus.api.gm.app;

import cloud.xcan.angus.api.gm.app.dto.AppOpenCancelDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenRenewDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface AppOpenInnerRemote {

  @Operation(summary = "Open application.", operationId = "app:open:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Open successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/innerapi/v1/appopen")
  ApiLocaleResult<IdKey<Long, Object>> open(@Valid @RequestBody AppOpenDto dto);

  @Operation(summary = "Renewal the opened application.", operationId = "app:open:renew:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Renew successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PatchMapping(value = "/innerapi/v1/appopen/renew")
  ApiLocaleResult<?> renew(@Valid @RequestBody AppOpenRenewDto dto);

  @Operation(summary = "Cancel the opened application.", operationId = "app:open:cancel:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cancel successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @DeleteMapping(value = "/innerapi/v1/appopen/cancel")
  ApiLocaleResult<?> cancel(@Valid @RequestBody AppOpenCancelDto dto);

}
