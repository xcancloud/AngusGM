package cloud.xcan.angus.core.gm.interfaces.tag;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.TagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagStatsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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

/**
 * Tag REST controller
 */
@Tag(name = "Tag", description = "标签管理 - 标签的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/tags")
public class TagRest {

  @Resource
  private TagFacade tagFacade;

  // 创建
  @Operation(operationId = "createTag", summary = "创建标签", description = "创建新标签")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "标签创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<TagDetailVo> create(
      @Valid @RequestBody TagCreateDto dto) {
    return ApiLocaleResult.success(tagFacade.create(dto));
  }

  // 更新
  @Operation(operationId = "updateTag", summary = "更新标签", description = "更新标签基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<TagDetailVo> update(
      @Parameter(description = "标签ID") @PathVariable Long id,
      @Valid @RequestBody TagUpdateDto dto) {
    return ApiLocaleResult.success(tagFacade.update(id, dto));
  }

  // 修改状态 - 启用
  @Operation(operationId = "enableTag", summary = "启用标签", description = "启用指定标签")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "启用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/enable")
  public ApiLocaleResult<Void> enable(
      @Parameter(description = "标签ID") @PathVariable Long id) {
    tagFacade.enable(id);
    return ApiLocaleResult.success(null);
  }

  // 修改状态 - 禁用
  @Operation(operationId = "disableTag", summary = "禁用标签", description = "禁用指定标签")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "禁用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/disable")
  public ApiLocaleResult<Void> disable(
      @Parameter(description = "标签ID") @PathVariable Long id) {
    tagFacade.disable(id);
    return ApiLocaleResult.success(null);
  }

  // 删除
  @Operation(operationId = "deleteTag", summary = "删除标签", description = "删除指定标签")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "标签ID") @PathVariable Long id) {
    tagFacade.delete(id);
  }

  // 查询详细
  @Operation(operationId = "getTagDetail", summary = "获取标签详情", 
      description = "获取指定标签的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标签详情获取成功"),
      @ApiResponse(responseCode = "404", description = "标签不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<TagDetailVo> getDetail(
      @Parameter(description = "标签ID") @PathVariable Long id) {
    return ApiLocaleResult.success(tagFacade.getDetail(id));
  }

  // 查询列表
  @Operation(operationId = "getTagList", summary = "获取标签列表", 
      description = "获取标签列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标签列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<TagListVo>> list(
      @Valid @ParameterObject TagFindDto dto) {
    return ApiLocaleResult.success(tagFacade.list(dto));
  }

  // 查询统计
  @Operation(operationId = "getTagStats", summary = "获取标签统计数据", 
      description = "获取标签统计数据，包括总数、启用/禁用数量、分类数等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<TagStatsVo> getStats() {
    return ApiLocaleResult.success(tagFacade.getStats());
  }

  // 按分类查询
  @Operation(operationId = "getTagsByCategory", summary = "按分类获取标签", 
      description = "获取指定分类下的所有标签")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标签列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/category/{category}")
  public ApiLocaleResult<List<TagDetailVo>> getByCategory(
      @Parameter(description = "分类名称") @PathVariable String category) {
    return ApiLocaleResult.success(tagFacade.getByCategory(category));
  }
}
