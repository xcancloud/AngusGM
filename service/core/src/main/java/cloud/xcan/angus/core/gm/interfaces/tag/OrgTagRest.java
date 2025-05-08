package cloud.xcan.angus.core.gm.interfaces.tag;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.OrgTagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagSearchDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
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

@Tag(name = "OrgTag")
@Validated
@RestController
@RequestMapping("/api/v1/org/tag")
public class OrgTagRest {

  @Resource
  private OrgTagFacade orgTagFacade;

  @Operation(description = "Add tag", operationId = "org:tag:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<OrgTagAddDto> dto) {
    return ApiLocaleResult.success(orgTagFacade.add(dto));
  }

  @Operation(description = "Update tags", operationId = "org:tag:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<OrgTagUpdateDto> dto) {
    orgTagFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete tags", operationId = "org:tag:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    orgTagFacade.delete(ids);
  }

  @Operation(description = "Query the detail of tag", operationId = "org:tag:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<OrgTagDetailVo> detail(
      @Parameter(name = "id", description = "Org tag id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(orgTagFacade.detail(id));
  }

  @Operation(description = "Query the list of tags", operationId = "org:tag:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<OrgTagDetailVo>> list(@Valid @ParameterObject OrgTagFindDto dto) {
    return ApiLocaleResult.success(orgTagFacade.list(dto));
  }

  @Operation(description = "Fulltext search tags", operationId = "org:tag:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<OrgTagDetailVo>> search(@Valid @ParameterObject OrgTagSearchDto dto) {
    return ApiLocaleResult.success(orgTagFacade.search(dto));
  }

}
