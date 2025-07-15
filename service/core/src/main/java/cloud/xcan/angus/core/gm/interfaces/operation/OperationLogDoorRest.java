package cloud.xcan.angus.core.gm.interfaces.operation;

import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.OperationLogFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "OperationLogInner", description = "Provides a unified api for collecting and recording user operation logs")
@RestController
@RequestMapping("/innerapi/v1/log/operation")
public class OperationLogDoorRest {

  @Resource
  private OperationLogFacade optionFacade;

  @Operation(summary = "Add user operation logs", operationId = "log:operation:add:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully create")})
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @RequestBody List<UserOperation> operations) {
    return ApiLocaleResult.success(optionFacade.add(operations));
  }

}
