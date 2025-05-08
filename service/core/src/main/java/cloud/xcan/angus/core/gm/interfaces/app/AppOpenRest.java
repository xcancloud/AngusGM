package cloud.xcan.angus.core.gm.interfaces.app;

import cloud.xcan.angus.api.gm.app.dto.AppOpenCancelDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenRenewDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppOpenFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.open.AppOpenFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.open.AppOpenVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "AppOpen", description =
    "Enable automated onboarding and configuration of applications, "
        + "granting authorized access to resources based on organizational/user permissions while enforcing security policies.")
@OperationClient
@Validated
@RestController
@RequestMapping("/api/v1/appopen")
public class AppOpenRest {

  @Resource
  private AppOpenFacade appOpenFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Open application.", operationId = "app:open")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Open successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> open(@Valid @RequestBody AppOpenDto dto) {
    return ApiLocaleResult.success(appOpenFacade.open(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Renewal the opened application.", operationId = "app:open:renew")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Renew successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PatchMapping("/renew")
  public ApiLocaleResult<?> renew(@Valid @RequestBody AppOpenRenewDto dto) {
    appOpenFacade.renew(dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(description = "Cancel the opened application.", operationId = "app:open:cancel")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cancel successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @DeleteMapping("/cancel")
  public ApiLocaleResult<?> cancel(@Valid @RequestBody AppOpenCancelDto dto) {
    appOpenFacade.cancel(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the list of opened application.", operationId = "app:open:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/list")
  public ApiLocaleResult<PageResult<AppOpenVo>> list(@Valid @ParameterObject AppOpenFindDto dto) {
    return ApiLocaleResult.success(appOpenFacade.list(dto));
  }
}
