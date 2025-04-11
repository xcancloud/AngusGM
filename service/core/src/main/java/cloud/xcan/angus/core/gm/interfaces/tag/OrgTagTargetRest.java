package cloud.xcan.angus.core.gm.interfaces.tag;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.OrgTagTargetFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetDetailVo;
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

@Tag(name = "OrgTagTarget")
@Validated
@RestController
@RequestMapping("/api/v1/org/tag")
public class OrgTagTargetRest {

  @Resource
  private OrgTagTargetFacade orgTagTargetFacade;

  @Operation(description = "Add the tag to targets", operationId = "org:tag:target:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/target")
  public ApiLocaleResult<List<IdKey<Long, Object>>> targetAdd(
      @Parameter(name = "id", description = "OrgTag id", required = true) @PathVariable("id") Long tagId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody LinkedHashSet<OrgTagTargetAddDto> dto) {
    return ApiLocaleResult.success(orgTagTargetFacade.targetAdd(tagId, dto));
  }

  @Operation(description = "Delete the tag of targets", operationId = "org:tag:target:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/target")
  public void targetDelete(
      @Parameter(name = "id", description = "Org tag id", required = true) @PathVariable("id") Long tagId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("targetIds") HashSet<Long> targetIds) {
    orgTagTargetFacade.targetDelete(tagId, targetIds);
  }

  @Operation(description = "Query the targets list of tag", operationId = "org:tag:target:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/target")
  public ApiLocaleResult<PageResult<OrgTagTargetDetailVo>> targetList(
      @Valid OrgTagTargetFindDto dto) {
    return ApiLocaleResult.success(orgTagTargetFacade.targetList(dto));
  }
}
