package cloud.xcan.angus.core.gm.interfaces.dept;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_RELATION_QUOTA;

import cloud.xcan.angus.core.gm.interfaces.dept.facade.DeptTagFacade;
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

@Tag(name = "Department Tag", description = "REST API endpoints for assigning tags to departments for resource categorization and access control grouping")
@Validated
@RestController
@RequestMapping("/api/v1/dept")
public class DeptTagRest {

  @Resource
  private DeptTagFacade deptTagFacade;

  @Operation(summary = "Assign tags to department", operationId = "dept:tag:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Tags assigned to department successfully"),
      @ApiResponse(responseCode = "404", description = "Department not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/tag")
  public ApiLocaleResult<List<IdKey<Long, Object>>> tagAdd(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody LinkedHashSet<Long> tagIds) {
    return ApiLocaleResult.success(deptTagFacade.tagAdd(deptId, tagIds));
  }

  @Operation(summary = "Replace department tags", operationId = "department:tag:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Department tags replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Department not found")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/tag")
  public ApiLocaleResult<?> tagReplace(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @Size(max = MAX_RELATION_QUOTA) @RequestBody LinkedHashSet<Long> tagIds) {
    deptTagFacade.tagReplace(deptId, tagIds);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Remove tags from department", operationId = "dept:tag:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Tags removed from department successfully"),
      @ApiResponse(responseCode = "404", description = "Department not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/tag")
  public void tagDelete(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("tagIds") HashSet<Long> tagIds) {
    deptTagFacade.tagDelete(deptId, tagIds);
  }

  @Operation(summary = "Retrieve department tag list with filtering and pagination", operationId = "dept:tag:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Department tag list retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Department not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{id}/tag")
  public ApiLocaleResult<PageResult<OrgTagTargetVo>> tagList(
      @Parameter(name = "id", description = "Department identifier", required = true) @PathVariable("id") Long deptId,
      @Valid @ParameterObject OrgTargetTagFindDto dto) {
    return ApiLocaleResult.success(deptTagFacade.tagList(deptId, dto));
  }

}
