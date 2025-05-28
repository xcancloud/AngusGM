package cloud.xcan.angus.core.gm.interfaces.app;

import cloud.xcan.angus.api.gm.app.dto.AppOpenCancelDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenDto;
import cloud.xcan.angus.api.gm.app.dto.AppOpenRenewDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppOpenFacade;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "AppOpenInner", description =
    "Used for opening applications through system calls in the background (/innerapi). "
        + "Enable automated onboarding and configuration of applications, granting authorized access to resources "
        + "based on organizational/user permissions while enforcing security policies.")
@Conditional(CloudServiceEditionCondition.class)
@Validated
@RestController
@RequestMapping("/innerapi/v1/appopen")
public class AppOpenDoorRest {

  @Resource
  private AppOpenFacade appOpenFacade;

  @Operation(summary = "Open application.", operationId = "app:open:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Open successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> open(@Valid @RequestBody AppOpenDto dto) {
    return ApiLocaleResult.success(appOpenFacade.open(dto));
  }

  @Operation(summary = "Renewal the opened application.", operationId = "app:open:renew:inner")
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

  @Operation(summary = "Cancel the opened application.", operationId = "app:open:cancel:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cancel successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @DeleteMapping("/cancel")
  public ApiLocaleResult<?> cancel(@Valid @RequestBody AppOpenCancelDto dto) {
    appOpenFacade.cancel(dto);
    return ApiLocaleResult.success();
  }
}
