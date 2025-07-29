package cloud.xcan.angus.core.gm.interfaces.api;

import static io.swagger.v3.oas.models.extension.ExtensionKey.RESOURCE_NAME_KEY;

import cloud.xcan.angus.core.gm.interfaces.api.facade.ApiLogFacade;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogInfoVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Api Log", description = "API for inspecting recorded API transaction details for auditing, debugging, and monitoring")
@Extension(properties = @ExtensionProperty(name = RESOURCE_NAME_KEY, value = "ApiLog"))
@Validated
@RestController
@RequestMapping("/api/v1/log/api")
public class ApiLogRest {

  @Resource
  private ApiLogFacade apiLogFacade;

  @Operation(summary = "Retrieve detailed information of a specific API request log", operationId = "log:api:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "API log details successfully retrieved"),
      @ApiResponse(responseCode = "404", description = "API log record not found")})
  @GetMapping("/{id}")
  public ApiLocaleResult<ApiLogDetailVo> detail(
      @Parameter(name = "id", description = "API log record identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apiLogFacade.detail(id));
  }

  @Operation(summary = "Query and retrieve paginated list of API request logs", operationId = "log:api:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "API log list successfully retrieved")})
  @GetMapping
  public ApiLocaleResult<PageResult<ApiLogInfoVo>> list(@Valid @ParameterObject ApiLogFindDto dto) {
    return ApiLocaleResult.success(apiLogFacade.list(dto));
  }

}
