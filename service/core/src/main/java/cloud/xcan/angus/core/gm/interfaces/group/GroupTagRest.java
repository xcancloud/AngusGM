package cloud.xcan.angus.core.gm.interfaces.group;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupTagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetVo;
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
import java.util.LinkedHashSet;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "GroupTag", description = "Assigns tags to group for resource categorization, or query access control grouping.")
@Validated
@RestController
@RequestMapping("/api/v1/group")
public class GroupTagRest {

  @Resource
  private GroupTagFacade groupTagFacade;

  @Operation(summary = "Add the tags of group.", operationId = "group:tag:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/tag")
  public ApiLocaleResult<List<IdKey<Long, Object>>> tagAdd(
      @Parameter(name = "id", description = "Group id", required = true) @PathVariable("id") Long groupId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody LinkedHashSet<Long> tagIds) {
    return ApiLocaleResult.success(groupTagFacade.tagAdd(groupId, tagIds));
  }

  @Operation(summary = "Replace the tags of group.", operationId = "group:tag:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping("/{id}/tag")
  public ApiLocaleResult<?> tagReplace(
      @Parameter(name = "id", description = "Group id", required = true) @PathVariable("id") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_RELATION_QUOTA) @RequestBody LinkedHashSet<Long> tagIds) {
    groupTagFacade.tagReplace(groupId, tagIds);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete the tags of group.", operationId = "group:tag:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/tag")
  public void tagDelete(
      @Parameter(name = "id", description = "Group id", required = true) @PathVariable("id") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("tagIds") HashSet<Long> tagIds) {
    groupTagFacade.tagDelete(groupId, tagIds);
  }

  @Operation(summary = "Query the tags list of group.", operationId = "group:tag:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}/tag")
  public ApiLocaleResult<PageResult<OrgTagTargetVo>> tagList(
      @Parameter(name = "id", description = "Group id", required = true) @PathVariable("id") Long groupId,
      @Valid @ParameterObject OrgTargetTagFindDto dto) {
    return ApiLocaleResult.success(groupTagFacade.tagList(groupId, dto));
  }

}
