package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.common.result.ApiLocaleResult;
import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsSendBatchDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTestDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateStatusDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsProviderCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsRecordVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsSendVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsSendBatchVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTestVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsProviderVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
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

import java.util.List;

@Tag(name = "SMS", description = "短信消息 - 短信发送、模板管理、发送记录查询")
@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsRest {

    private final SmsFacade smsFacade;

    // ==================== 统计与记录 ====================

    @Operation(summary = "获取短信统计数据", description = "获取短信发送统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/stats")
    public ApiLocaleResult<SmsStatsVo> getStats() {
        return ApiLocaleResult.success(smsFacade.getStats());
    }

    @Operation(summary = "获取短信记录列表", description = "分页获取短信发送记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/records")
    public ApiLocaleResult<PageResult<SmsRecordVo>> listRecords(@ParameterObject SmsRecordFindDto dto) {
        return ApiLocaleResult.success(smsFacade.listRecords(dto));
    }

    // ==================== 短信发送 ====================

    @Operation(summary = "发送单条短信", description = "发送一条短信消息")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<SmsSendVo> send(@Valid @RequestBody SmsSendDto dto) {
        return ApiLocaleResult.success(smsFacade.send(dto));
    }

    @Operation(summary = "批量发送短信", description = "批量发送短信消息")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "批量发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/send-batch")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<SmsSendBatchVo> sendBatch(@Valid @RequestBody SmsSendBatchDto dto) {
        return ApiLocaleResult.success(smsFacade.sendBatch(dto));
    }

    @Operation(summary = "测试短信发送", description = "发送测试短信")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "测试发送成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/test")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<SmsTestVo> test(@Valid @RequestBody SmsTestDto dto) {
        return ApiLocaleResult.success(smsFacade.test(dto));
    }

    // ==================== 短信模板管理 ====================

    @Operation(summary = "获取短信模板列表", description = "分页获取短信模板列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/templates")
    public ApiLocaleResult<PageResult<SmsTemplateVo>> listTemplates(@ParameterObject SmsTemplateFindDto dto) {
        return ApiLocaleResult.success(smsFacade.listTemplates(dto));
    }

    @Operation(summary = "创建短信模板", description = "创建新的短信模板")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/templates")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<SmsTemplateVo> createTemplate(@Valid @RequestBody SmsTemplateCreateDto dto) {
        return ApiLocaleResult.success(smsFacade.createTemplate(dto));
    }

    @Operation(summary = "更新短信模板", description = "更新指定短信模板")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "模板不存在")
    })
    @PutMapping("/templates/{id}")
    public ApiLocaleResult<SmsTemplateVo> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody SmsTemplateUpdateDto dto) {
        return ApiLocaleResult.success(smsFacade.updateTemplate(id, dto));
    }

    @Operation(summary = "删除短信模板", description = "删除指定短信模板")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "模板不存在")
    })
    @DeleteMapping("/templates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        smsFacade.deleteTemplate(id);
    }

    @Operation(summary = "启用/禁用短信模板", description = "修改短信模板状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "状态更新成功"),
            @ApiResponse(responseCode = "404", description = "模板不存在")
    })
    @PatchMapping("/templates/{id}/status")
    public ApiLocaleResult<SmsTemplateStatusVo> updateTemplateStatus(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody SmsTemplateStatusDto dto) {
        return ApiLocaleResult.success(smsFacade.updateTemplateStatus(id, dto));
    }

    // ==================== 服务商配置 ====================

    @Operation(summary = "获取短信服务商配置", description = "获取所有短信服务商配置")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/providers")
    public ApiLocaleResult<List<SmsProviderVo>> listProviders() {
        return ApiLocaleResult.success(smsFacade.listProviders());
    }

    @Operation(summary = "创建服务商配置", description = "创建新的短信服务商配置")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping("/providers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<SmsProviderVo> createProvider(@Valid @RequestBody SmsProviderCreateDto dto) {
        return ApiLocaleResult.success(smsFacade.createProvider(dto));
    }
}
