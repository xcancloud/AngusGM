package cloud.xcan.angus.core.gm.interfaces.to;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.to.facade.TOUserFacade;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserVo;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@OperationClient
@PreAuthorize("@PPS.isOpClient()")
@Conditional(value = CloudServiceEditionCondition.class)
@Tag(name = "TOUser", description = "Provides an entry for managing operational users in the cloud service edition (SaaS).")
@Validated
@RestController
@RequestMapping("/api/v1/to/user")
public class TOUserRest {

  @Resource
  private TOUserFacade toUserFacade;

  @Operation(summary = "Add operation users.", operationId = "to:user:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<TOUserAddDto> dto) {
    return ApiLocaleResult.success(toUserFacade.add(dto));
  }

  @Operation(summary = "Delete operation users.", operationId = "to:user:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @RequestParam("userIds") @Size(max = MAX_BATCH_SIZE) HashSet<Long> userIds) {
    toUserFacade.delete(userIds);
  }

  @Operation(summary = "Query the detail of operation user.", operationId = "to:user:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<TOUserDetailVo> detail(
      @Parameter(name = "id", description = "User id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(toUserFacade.detail(id));
  }

  @Operation(summary = "Query the list of operation user.", operationId = "to:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<TOUserVo>> list(@Valid @ParameterObject TOUserFindDto dto) {
    return ApiLocaleResult.success(toUserFacade.list(dto));
  }

}
