package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsChannelFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.channel.SmsChannelVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
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


@Tag(name = "SmsChannel", description =
    "Provides management for third-party SMS channel configuration. "
        + "If the SMS cannot be sent, please confirm whether the corresponding plugin for the SMS channel is installed or if the SMS template is correct.")
@Validated
@RestController
@RequestMapping("/api/v1/sms/channel")
public class SmsChannelRest {

  @Resource
  private SmsChannelFacade smsChannelFacade;

  @Operation(summary = "Update sms channel.", operationId = "sms:channel:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody SmsChannelUpdateDto dto) {
    smsChannelFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Enabled or disable sms channel", operationId = "sms:channel:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully")})
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(@Valid @RequestBody EnabledOrDisabledDto dto) {
    smsChannelFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the list of sms channel.", operationId = "sms:channel:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<SmsChannelVo>> list(@Valid @ParameterObject SmsChannelFindDto dto) {
    return ApiLocaleResult.success(smsChannelFacade.list(dto));
  }

  @Operation(summary = "Query the detail of sms channel.", operationId = "sms:channel:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<SmsChannelVo> detail(
      @Parameter(name = "id", description = "SMS channel id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(smsChannelFacade.detail(id));
  }

}
