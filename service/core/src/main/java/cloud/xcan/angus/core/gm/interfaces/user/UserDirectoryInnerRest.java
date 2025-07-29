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
@Tag(name = "User Directory - Internal", description = "Internal API for system-to-system communication to synchronize user and group data with external directory services (LDAP, Active Directory, etc.)")
@Validated
@RestController
@RequestMapping("/innerapi/v1/user/directory")
public class UserDirectoryInnerRest {

  @Resource
  private UserDirectoryFacade userDirectoryFacade;

  @PreAuthorize("@PPS.isCloudTenantSecurity()")
  @Operation(summary = "Synchronize users and groups from all configured directory services", operationId = "user:directory:sync:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Directory synchronization completed successfully"),
      @ApiResponse(responseCode = "404", description = "Directory service configuration not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PutMapping("/sync")
  public ApiLocaleResult<Map<String, UserDirectorySyncVo>> sync() {
    return ApiLocaleResult.success(userDirectoryFacade.sync());
  }
}
