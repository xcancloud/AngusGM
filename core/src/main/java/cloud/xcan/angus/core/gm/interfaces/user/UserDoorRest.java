package cloud.xcan.angus.core.gm.interfaces.user;


import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAddDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserDoor")
@Validated
@RestController
@RequestMapping("/innerapi/v1/user")
public class UserDoorRest {

  @Resource
  private UserFacade userFacade;

  @Operation(description = "Add user.", operationId = "user:add:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{source}")
  public ApiLocaleResult<IdKey<Long, Object>> add(
      @Parameter(name = "source", description = "User source", required = true) @PathVariable("source") String source,
      @Valid @RequestBody UserAddDto dto) {
    return ApiLocaleResult.success(userFacade.add(dto, UserSource.valueOf(source)));
  }

}
