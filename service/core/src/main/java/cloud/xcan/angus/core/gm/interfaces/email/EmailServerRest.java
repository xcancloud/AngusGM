package cloud.xcan.angus.core.gm.interfaces.email;

import static cloud.xcan.angus.api.commonlink.EmailConstant.MAX_MAIL_SERVER_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailServerFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerAddDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerEnabledCheckDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.server.ServerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.server.ServerDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "EmailServer", description =
    "Email server configuration management, authentication protocols, "
        + "and delivery policies to ensure reliable, compliant email communication across systems")
@Validated
@RestController
@RequestMapping("/api/v1/email/server")
public class EmailServerRest {

  @Resource
  private EmailServerFacade emailServerFacade;

  @Operation(summary = "Add email server", operationId = "email:server:add")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody ServerAddDto dto) {
    return ApiLocaleResult.success(emailServerFacade.add(dto));
  }

  @Operation(summary = "Update email server", operationId = "email:server:update")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Updated successfully")})
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody ServerUpdateDto dto) {
    emailServerFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace email server", operationId = "email:server:replace")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping
  public ApiLocaleResult<IdKey<Long, Object>> replace(@Valid @RequestBody ServerReplaceDto dto) {
    return ApiLocaleResult.success(emailServerFacade.replace(dto));
  }

  @Operation(summary = "Delete email server", operationId = "email:server:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_MAIL_SERVER_QUOTA) @RequestParam("ids") HashSet<Long> ids) {
    emailServerFacade.delete(ids);
  }

  @Operation(summary = "Enable or disable email server", operationId = "email:server:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enable(@Valid @RequestBody EnabledOrDisabledDto dto) {
    emailServerFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Check the enabled email server", operationId = "email:server:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/check")
  public ApiLocaleResult<?> checkEnable(@ParameterObject ServerEnabledCheckDto dto) {
    emailServerFacade.checkEnable(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the detail of email server", operationId = "email:server:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Email server does not exist")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<ServerDetailVo> detail(
      @Parameter(name = "id", description = "Email server ID", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(emailServerFacade.detail(id));
  }

  @Operation(summary = "Query the list of email server", operationId = "email:server:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<ServerDetailVo>> list(@Valid @ParameterObject ServerFindDto dto) {
    return ApiLocaleResult.success(emailServerFacade.list(dto));
  }

}
