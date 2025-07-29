package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.template.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.template.SmsTemplateDetailVo;
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


@Tag(name = "Sms Template", description = "SMS template management system. Standardizes SMS content creation through reusable "
    + "templates with dynamic variables and multi-language support, maintaining brand consistency and operational efficiency")
@Validated
@RestController
@RequestMapping("/api/v1/sms/template")
public class SmsTemplateRest {

  @Resource
  private SmsTemplateFacade smsTemplateFacade;

  @Operation(summary = "Update SMS template configuration", operationId = "sms:template:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS template configuration updated successfully"),
      @ApiResponse(responseCode = "404", description = "SMS template not found")})
  @PatchMapping("/{id}")
  public ApiLocaleResult<?> update(
      @Parameter(name = "id", description = "SMS template unique identifier", required = true) @PathVariable("id") Long id,
      @RequestBody SmsTemplateUpdateDto dto) {
    smsTemplateFacade.update(id, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get detailed SMS template information", operationId = "sms:template:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS template details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "SMS template not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<SmsTemplateDetailVo> detail(
      @Parameter(name = "id", description = "SMS template unique identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(smsTemplateFacade.detail(id));
  }

  @Operation(summary = "Get paginated list of SMS templates", operationId = "sms:template:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS template list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<SmsTemplateDetailVo>> list(@Valid @ParameterObject SmsTemplateFindDto dto) {
    return ApiLocaleResult.success(smsTemplateFacade.list(dto));
  }

}
