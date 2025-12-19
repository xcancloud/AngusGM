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


@Tag(name = "Sms Channel", description =
    "SMS channel configuration management. Provides management for third-party SMS channel configuration, "
        + "including API credentials, endpoints, and channel status. If SMS cannot be sent, please verify "
        + "that the corresponding SMS channel plugin is installed and SMS templates are correctly configured")
@Validated
@RestController
@RequestMapping("/api/v1/sms/channel")
public class SmsChannelRest {

  @Resource
  private SmsChannelFacade smsChannelFacade;

  @Operation(summary = "Update SMS channel configuration", operationId = "sms:channel:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS channel configuration updated successfully"),
      @ApiResponse(responseCode = "404", description = "SMS channel not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody SmsChannelUpdateDto dto) {
    smsChannelFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Enable or disable SMS channel", operationId = "sms:channel:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS channel status updated successfully")})
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(@Valid @RequestBody EnabledOrDisabledDto dto) {
    smsChannelFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get detailed SMS channel information", operationId = "sms:channel:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS channel details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "SMS channel not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<SmsChannelVo> detail(
      @Parameter(name = "id", description = "SMS channel unique identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(smsChannelFacade.detail(id));
  }

  @Operation(summary = "Get paginated list of SMS channels", operationId = "sms:channel:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS channel list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<SmsChannelVo>> list(
      @Valid @ParameterObject SmsChannelFindDto dto) {
    return ApiLocaleResult.success(smsChannelFacade.list(dto));
  }


}
