package cloud.xcan.angus.core.gm.interfaces.to;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.to.facade.TORoleFacade;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleSearchDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleVo;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.annotations.OperationClient;
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
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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


@OperationClient
@PreAuthorize("@PPS.isOpClient()")
@Conditional(value = CloudServiceEditionCondition.class)
@Tag(name = "TORole", description =
    "Operational role management. Streamlines organizational efficiency by defining responsibilities, "
        + "coordinating workflows, and aligning permissions with business objectives")
@Validated
@RestController
@RequestMapping("/api/v1/to/role")
public class TORoleRest {

  @Resource
  private TORoleFacade toRoleFacade;

  @Operation(summary = "Add operation roles", operationId = "to:role:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<TORoleAddDto> dto) {
    return ApiLocaleResult.success(toRoleFacade.add(dto));
  }

  @Operation(summary = "Update operation roles", operationId = "to:role:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<TORoleUpdateDto> dto) {
    toRoleFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace operation roles", operationId = "to:role:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> replace(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<TORoleReplaceDto> dto) {
    return ApiLocaleResult.success(toRoleFacade.replace(dto));
  }

  @Operation(summary = "Delete operation roles", operationId = "to:role:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    toRoleFacade.delete(ids);
  }

  @Operation(summary = "Enable or disable the operation roles", operationId = "to:role:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<EnabledOrDisabledDto> dto) {
    toRoleFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the detail of operation role", operationId = "to:role:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{idOrCode}")
  public ApiLocaleResult<TORoleDetailVo> detail(
      @Parameter(name = "idOrCode", description = "Role code or id", required = true) @PathVariable("idOrCode") String idOrCode) {
    return ApiLocaleResult.success(toRoleFacade.detail(idOrCode));
  }

  @Operation(summary = "Query the list of operation role", operationId = "to:role:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<TORoleVo>> list(@Valid @ParameterObject TORoleFindDto dto) {
    return ApiLocaleResult.success(toRoleFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of operation role", operationId = "to:role:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<TORoleVo>> search(@Valid @ParameterObject TORoleSearchDto dto) {
    return ApiLocaleResult.success(toRoleFacade.search(dto));
  }

}
