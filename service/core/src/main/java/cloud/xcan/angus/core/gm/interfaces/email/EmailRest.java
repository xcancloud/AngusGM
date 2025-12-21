package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendBatchDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendCustomDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateStatusDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailRecordVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSendBatchVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSendVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpTestVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTrackingVo;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email", description = "电子邮件 - 邮件发送、模板管理、发送记录查询")
@Validated
@RestController
@RequestMapping("/api/v1/email")
public class EmailRest {

  @Resource
  private EmailFacade emailFacade;

  @Operation(operationId = "sendEmail", summary = "发送单封邮件", description = "使用模板发送一封邮件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发送成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send")
  public ApiLocaleResult<EmailSendVo> send(@Valid @RequestBody EmailSendDto dto) {
    return ApiLocaleResult.success(emailFacade.send(dto));
  }

  @Operation(operationId = "sendEmailBatch", summary = "批量发送邮件", description = "批量发送邮件消息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量发送成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send-batch")
  public ApiLocaleResult<EmailSendBatchVo> sendBatch(@Valid @RequestBody EmailSendBatchDto dto) {
    return ApiLocaleResult.success(emailFacade.sendBatch(dto));
  }

  @Operation(operationId = "sendEmailCustom", summary = "发送自定义邮件", description = "不使用模板发送自定义邮件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发送成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send-custom")
  public ApiLocaleResult<EmailSendVo> sendCustom(@Valid @RequestBody EmailSendCustomDto dto) {
    return ApiLocaleResult.success(emailFacade.sendCustom(dto));
  }

  @Operation(operationId = "createEmailTemplate", summary = "创建邮件模板", description = "创建新的邮件模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/templates")
  public ApiLocaleResult<EmailTemplateVo> createTemplate(@Valid @RequestBody EmailTemplateCreateDto dto) {
    return ApiLocaleResult.success(emailFacade.createTemplate(dto));
  }

  @Operation(operationId = "updateEmailTemplate", summary = "更新邮件模板", description = "更新指定邮件模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "模板不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/templates/{id}")
  public ApiLocaleResult<EmailTemplateVo> updateTemplate(
      @Parameter(description = "模板ID") @PathVariable Long id,
      @Valid @RequestBody EmailTemplateUpdateDto dto) {
    return ApiLocaleResult.success(emailFacade.updateTemplate(id, dto));
  }

  @Operation(operationId = "updateEmailSmtpConfig", summary = "更新SMTP配置", description = "更新SMTP服务器配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/smtp")
  public ApiLocaleResult<EmailSmtpVo> updateSmtpConfig(@Valid @RequestBody EmailSmtpUpdateDto dto) {
    return ApiLocaleResult.success(emailFacade.updateSmtpConfig(dto));
  }

  @Operation(operationId = "updateEmailTemplateStatus", summary = "启用/禁用邮件模板", description = "修改邮件模板状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功"),
      @ApiResponse(responseCode = "404", description = "模板不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/templates/{id}/status")
  public ApiLocaleResult<EmailTemplateStatusVo> updateTemplateStatus(
      @Parameter(description = "模板ID") @PathVariable Long id,
      @Valid @RequestBody EmailTemplateStatusDto dto) {
    return ApiLocaleResult.success(emailFacade.updateTemplateStatus(id, dto));
  }

  @Operation(operationId = "deleteEmailTemplate", summary = "删除邮件模板", description = "删除指定邮件模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "404", description = "模板不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/templates/{id}")
  public void deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
    emailFacade.deleteTemplate(id);
  }

  @Operation(operationId = "getEmailStats", summary = "获取邮件统计数据", description = "获取邮件发送统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<EmailStatsVo> getStats() {
    return ApiLocaleResult.success(emailFacade.getStats());
  }

  @Operation(operationId = "getEmailRecords", summary = "获取邮件记录列表", description = "分页获取邮件发送记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records")
  public ApiLocaleResult<PageResult<EmailRecordVo>> listRecords(@ParameterObject EmailRecordFindDto dto) {
    return ApiLocaleResult.success(emailFacade.listRecords(dto));
  }

  @Operation(operationId = "getEmailTrackingStats", summary = "获取邮件打开/点击统计", description = "获取指定邮件的打开和点击统计")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "邮件不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/stats")
  public ApiLocaleResult<EmailTrackingVo> getEmailStats(
      @Parameter(description = "邮件ID") @PathVariable Long id) {
    return ApiLocaleResult.success(emailFacade.getEmailStats(id));
  }

  @Operation(operationId = "getEmailTemplates", summary = "获取邮件模板列表", description = "分页获取邮件模板列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/templates")
  public ApiLocaleResult<PageResult<EmailTemplateVo>> listTemplates(@ParameterObject EmailTemplateFindDto dto) {
    return ApiLocaleResult.success(emailFacade.listTemplates(dto));
  }

  @Operation(operationId = "getEmailSmtpConfig", summary = "获取SMTP配置", description = "获取当前SMTP服务器配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/smtp")
  public ApiLocaleResult<EmailSmtpVo> getSmtpConfig() {
    return ApiLocaleResult.success(emailFacade.getSmtpConfig());
  }

  @Operation(operationId = "testEmailSmtpConnection", summary = "测试SMTP连接", description = "测试SMTP服务器连接")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/smtp/test")
  public ApiLocaleResult<EmailSmtpTestVo> testSmtpConnection(@Valid @RequestBody EmailSmtpTestDto dto) {
    return ApiLocaleResult.success(emailFacade.testSmtpConnection(dto));
  }
}
