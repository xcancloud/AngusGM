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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "EmailTemplate", description = "Standardizes email content creation through reusable "
    + "templates with dynamic variables and multi-language support, maintaining brand consistency and operational efficiency.")
@Validated
@RestController
@RequestMapping("/api/v1/email/template/")
public class EmailTemplateRest {

  @Resource
  private EmailTemplateFacade templateFacade;

  @Operation(description = "Update email template.", operationId = "email:template:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully")})
  @PatchMapping(value = "/{id}")
  public ApiLocaleResult<?> update(
      @Parameter(name = "id", description = "Template id", required = true) @PathVariable("id") Long id,
      @RequestBody EmailTemplateUpdateDto dto) {
    templateFacade.update(id, dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the detail of email template.", operationId = "email:template:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<EmailTemplateDetailVo> detail(
      @Parameter(name = "id", description = "Template id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(templateFacade.detail(id));
  }

  @Operation(description = "Query the list of email template.", operationId = "email:template:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<EmailTemplateDetailVo>> list(@Valid @ParameterObject EmailTemplateFindDto dto) {
    return ApiLocaleResult.success(templateFacade.list(dto));
  }

}
