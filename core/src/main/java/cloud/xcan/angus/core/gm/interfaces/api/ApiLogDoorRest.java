package cloud.xcan.angus.core.gm.interfaces.api;

import static io.swagger.v3.oas.models.extension.ExtensionKey.RESOURCE_NAME_KEY;

import cloud.xcan.angus.core.event.source.ApiLog;
import cloud.xcan.angus.core.gm.interfaces.api.facade.ApiLogFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "ApiLogDoor", description = "Aggregates and stores API interaction data for analysis.")
@Extension(properties = @ExtensionProperty(name = RESOURCE_NAME_KEY, value = "ApiLogDoor"))
@RestController
@RequestMapping("/doorapi/v1/log/api")
public class ApiLogDoorRest {

  @Resource
  private ApiLogFacade apiLogFacade;

  @Operation(description = "Receives and save api request logs.", operationId = "log:api:add:door")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully created")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(@RequestBody List<ApiLog> apiLogs) {
    return ApiLocaleResult.success(apiLogFacade.add(apiLogs));
  }

}
