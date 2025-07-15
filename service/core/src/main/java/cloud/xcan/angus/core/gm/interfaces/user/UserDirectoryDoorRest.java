package cloud.xcan.angus.core.gm.interfaces.user;


import cloud.xcan.angus.core.gm.interfaces.user.facade.UserDirectoryFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.directory.UserDirectorySyncVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "UserDirectoryInner", description = "Internal system call for synchronizing user and group information with the directory service api")
@Validated
@RestController
@RequestMapping("/innerapi/v1/user/directory")
public class UserDirectoryDoorRest {

  @Resource
  private UserDirectoryFacade userDirectoryFacade;

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(summary = "Synchronize the users and groups from user directories", operationId = "user:directory:sync:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Synchronize successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PutMapping("/sync")
  public ApiLocaleResult<Map<String, UserDirectorySyncVo>> sync() {
    return ApiLocaleResult.success(userDirectoryFacade.sync());
  }
}
