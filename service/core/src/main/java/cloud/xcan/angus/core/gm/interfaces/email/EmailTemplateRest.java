package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.template.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.template.EmailTemplateDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Email Template", description = "REST API endpoints for managing email templates with dynamic variables and multi-language support to maintain brand consistency and operational efficiency")
@Validated
@RestController
@RequestMapping("/api/v1/email/template/")
public class EmailTemplateRest {

  @Resource
  private EmailTemplateFacade templateFacade;

  @Operation(summary = "Update email template configuration", operationId = "email:template:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email template updated successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping(value = "/{id}")
  public ApiLocaleResult<?> update(
      @Parameter(name = "id", description = "Email template identifier", required = true) @PathVariable("id") Long id,
      @RequestBody EmailTemplateUpdateDto dto) {
    templateFacade.update(id, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Retrieve detailed email template information", operationId = "email:template:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email template details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Email template not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<EmailTemplateDetailVo> detail(
      @Parameter(name = "id", description = "Email template identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(templateFacade.detail(id));
  }

  @Operation(summary = "Retrieve email template list with filtering and pagination", operationId = "email:template:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email template list retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<EmailTemplateDetailVo>> list(@Valid @ParameterObject EmailTemplateFindDto dto) {
    return ApiLocaleResult.success(templateFacade.list(dto));
  }

}
