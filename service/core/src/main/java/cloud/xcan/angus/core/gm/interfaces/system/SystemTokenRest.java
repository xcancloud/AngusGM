package cloud.xcan.angus.core.gm.interfaces.system;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.SystemTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenValueVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
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
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "SystemToken", description =
    "System access tokens are digital \"ID cards\" that temporarily "
        + "grant users or apps specific permissions to access systems or data. They act like a secure, "
        + "time-limited passcode—unlike traditional passwords, tokens verify identity AND control exactly "
        + "what actions are allowed (e.g. \"read-only access for 1 hour\")")
@Validated
@RestController
@RequestMapping("/api/v1/system/token")
public class SystemTokenRest {

  @Resource
  private SystemTokenFacade systemTokenFacade;

  @Operation(summary = "Add system token", operationId = "system:token:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<SystemTokenValueVo> add(@Valid @RequestBody SystemTokenAddDto dto) {
    return ApiLocaleResult.success(systemTokenFacade.add(dto));
  }

  @Operation(summary = "Delete system tokens", operationId = "system:token:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    systemTokenFacade.delete(ids);
  }

  @Operation(summary = "Query the authorization of system token", operationId = "system:token:auth:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{id}/auth")
  public ApiLocaleResult<SystemTokenDetailVo> auth(
      @Parameter(name = "id", description = "System token id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(systemTokenFacade.auth(id));
  }

  @Operation(summary = "Query the value of system token", operationId = "system:token:value:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{id}/value")
  public ApiLocaleResult<SystemTokenValueVo> value(
      @Parameter(name = "id", description = "System token id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(systemTokenFacade.value(id));
  }

  @Operation(summary = "Query all system tokens", operationId = "system:token:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<List<SystemTokenInfoVo>> list() {
    return ApiLocaleResult.success(systemTokenFacade.list());
  }

}
