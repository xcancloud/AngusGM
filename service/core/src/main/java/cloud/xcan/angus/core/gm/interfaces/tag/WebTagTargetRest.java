package cloud.xcan.angus.core.gm.interfaces.tag;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.WebTagTargetFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagTargetDetailVo;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
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


@Validated
@Tag(name = "Web Tag Target", description = "Web application tag-target relationship management. Maintains and manages associations between web application tags and their target resources for effective categorization")
@RestController
@RequestMapping("/api/v1/web/tag")
public class WebTagTargetRest {

  @Resource
  private WebTagTargetFacade webTagTargetFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Assign web application tags to target resources", operationId = "web:tag:target:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Web application tags assigned to targets successfully"),
      @ApiResponse(responseCode = "404", description = "Web application tag or target not found")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{id}/target")
  public ApiLocaleResult<List<IdKey<Long, Object>>> targetAdd(
      @Parameter(name = "id", description = "Web application tag unique identifier", required = true) @PathVariable("id") Long tagId,
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody LinkedHashSet<WebTagTargetAddDto> dto) {
    return ApiLocaleResult.success(webTagTargetFacade.targetAdd(tagId, dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Remove web application tags from target resources", operationId = "web:tag:target:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Web application tags removed from targets successfully"),
      @ApiResponse(responseCode = "404", description = "Web application tag or target not found")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}/target")
  public void targetDelete(
      @Parameter(name = "id", description = "Web application tag unique identifier", required = true) @PathVariable("id") Long tagId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("targetIds") HashSet<@Min(1) Long> targetIds) {
    webTagTargetFacade.targetDelete(tagId, targetIds);
  }

  @Operation(summary = "Get paginated list of web application tag-target relationships", operationId = "web:tag:target:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Web application tag-target relationships retrieved successfully")})
  @GetMapping("/{id}/target")
  public ApiLocaleResult<PageResult<WebTagTargetDetailVo>> targetList(
      @Valid @ParameterObject WebTagTargetFindDto dto) {
    return ApiLocaleResult.success(webTagTargetFacade.targetList(dto));
  }

}
