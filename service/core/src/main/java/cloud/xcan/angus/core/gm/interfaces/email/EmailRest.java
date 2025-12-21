package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.common.result.ApiLocaleResult;
import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Email", description = "电子邮件 - 邮件发送、模板管理、发送记录查询")
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailRest {

    private final EmailFacade emailFacade;

    // ==================== 统计与记录 ====================

    @Operation(summary = "获取邮件统计数据", description = "获取邮件发送统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/stats")
    public ApiLocaleResult<EmailStatsVo> getStats() {
        return ApiLocaleResult.success(emailFacade.getStats());
    }

    @Operation(summary = "获取邮件记录列表", description = "分页获取邮件发送记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/records")
    public ApiLocaleResult<PageResult<EmailRecordVo>> listRecords(@ParameterObject EmailRecordFindDto dto) {
        return ApiLocaleResult.success(emailFacade.listRecords(dto));
    }

    @Operation(summary = "获取邮件打开/点击统计", description = "获取指定邮件的打开和点击统计")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "邮件不存在")
    })
    @GetMapping("/{id}/stats")
    public ApiLocaleResult<EmailTrackingVo> getEmailStats(@Parameter(description = "邮件ID") @PathVariable Long id) {
        return ApiLocaleResult.success(emailFacade.getEmailStats(id));
    }

    // ==================== 邮件发送 ====================

    @Operation(summary = "发送单封邮件", description = "使用模板发送一封邮件")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<EmailSendVo> send(@Valid @RequestBody EmailSendDto dto) {
        return ApiLocaleResult.success(emailFacade.send(dto));
    }

    @Operation(summary = "批量发送邮件", description = "批量发送邮件消息")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "批量发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/send-batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<EmailSendBatchVo> sendBatch(@Valid @RequestBody EmailSendBatchDto dto) {
        return ApiLocaleResult.success(emailFacade.sendBatch(dto));
    }

    @Operation(summary = "发送自定义邮件", description = "不使用模板发送自定义邮件")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/send-custom")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<EmailSendVo> sendCustom(@Valid @RequestBody EmailSendCustomDto dto) {
        return ApiLocaleResult.success(emailFacade.sendCustom(dto));
    }

    // ==================== 邮件模板管理 ====================

    @Operation(summary = "获取邮件模板列表", description = "分页获取邮件模板列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/templates")
    public ApiLocaleResult<PageResult<EmailTemplateVo>> listTemplates(@ParameterObject EmailTemplateFindDto dto) {
        return ApiLocaleResult.success(emailFacade.listTemplates(dto));
    }

    @Operation(summary = "创建邮件模板", description = "创建新的邮件模板")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/templates")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<EmailTemplateVo> createTemplate(@Valid @RequestBody EmailTemplateCreateDto dto) {
        return ApiLocaleResult.success(emailFacade.createTemplate(dto));
    }

    @Operation(summary = "更新邮件模板", description = "更新指定邮件模板")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "模板不存在")
    })
    @PutMapping("/templates/{id}")
    public ApiLocaleResult<EmailTemplateVo> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody EmailTemplateUpdateDto dto) {
        return ApiLocaleResult.success(emailFacade.updateTemplate(id, dto));
    }

    @Operation(summary = "删除邮件模板", description = "删除指定邮件模板")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "模板不存在")
    })
    @DeleteMapping("/templates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        emailFacade.deleteTemplate(id);
    }

    @Operation(summary = "启用/禁用邮件模板", description = "修改邮件模板状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "状态更新成功"),
            @ApiResponse(responseCode = "404", description = "模板不存在")
    })
    @PatchMapping("/templates/{id}/status")
    public ApiLocaleResult<EmailTemplateStatusVo> updateTemplateStatus(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody EmailTemplateStatusDto dto) {
        return ApiLocaleResult.success(emailFacade.updateTemplateStatus(id, dto));
    }

    // ==================== SMTP配置 ====================

    @Operation(summary = "获取SMTP配置", description = "获取当前SMTP服务器配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/smtp")
    public ApiLocaleResult<EmailSmtpVo> getSmtpConfig() {
        return ApiLocaleResult.success(emailFacade.getSmtpConfig());
    }

    @Operation(summary = "更新SMTP配置", description = "更新SMTP服务器配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PutMapping("/smtp")
    public ApiLocaleResult<EmailSmtpVo> updateSmtpConfig(@Valid @RequestBody EmailSmtpUpdateDto dto) {
        return ApiLocaleResult.success(emailFacade.updateSmtpConfig(dto));
    }

    @Operation(summary = "测试SMTP连接", description = "测试SMTP服务器连接")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "测试完成"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/smtp/test")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<EmailSmtpTestVo> testSmtpConnection(@Valid @RequestBody EmailSmtpTestDto dto) {
        return ApiLocaleResult.success(emailFacade.testSmtpConnection(dto));
    }
}
