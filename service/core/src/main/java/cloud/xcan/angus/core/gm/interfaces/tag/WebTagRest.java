package cloud.xcan.angus.core.gm.interfaces.tag;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.WebTagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagSearchDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagDetailVo;
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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "WebTag")
@Validated
@RestController
@RequestMapping("/api/v1/web/tag")
public class WebTagRest {

  @Resource
  private WebTagFacade webTagFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Add tag", operationId = "web:tag:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<WebTagAddDto> dto) {
    return ApiLocaleResult.success(webTagFacade.add(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Update tags", operationId = "web:tag:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<WebTagUpdateDto> dto) {
    webTagFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Delete tags", operationId = "web:tag:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    webTagFacade.delete(ids);
  }

  @Operation(summary = "Query the detail of tag", operationId = "web:tag:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<WebTagDetailVo> detail(
      @Parameter(name = "id", description = "Org tag id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(webTagFacade.detail(id));
  }

  @Operation(summary = "Query the list of tags", operationId = "web:tag:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<WebTagDetailVo>> list(@Valid @ParameterObject WebTagFindDto dto) {
    return ApiLocaleResult.success(webTagFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of tags", operationId = "web:tag:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<WebTagDetailVo>> search(@Valid @ParameterObject WebTagSearchDto dto) {
    return ApiLocaleResult.success(webTagFacade.search(dto));
  }

}
