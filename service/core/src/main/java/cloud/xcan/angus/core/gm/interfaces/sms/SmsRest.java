package cloud.xcan.angus.core.gm.interfaces.sms;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelTestSendDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import org.springframework.http.HttpStatus;
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

@Tag(name = "sms", description = "Handles sms message delivery operations, and audit logging for tracking sent communications.")
@Validated
@RestController
@RequestMapping("/api/v1/sms")
public class SmsRest {

  @Resource
  private SmsFacade smsFacade;

  @Operation(description = "Send sms.", operationId = "sms:send")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sent successfully")})
  @PostMapping("/send")
  public ApiLocaleResult<?> send(@Valid @RequestBody SmsSendDto dto) {
    smsFacade.send(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Send the test sms to channel.", operationId = "sms:channel:test:send")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Test successfully")})
  @PostMapping("/channel/test")
  public ApiLocaleResult<?> channelTest(@Valid @RequestBody SmsChannelTestSendDto dto) {
    smsFacade.channelTest(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Check sms verification code.", operationId = "sms:verificationCode:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully check")})
  @GetMapping("/verificationCode/check")
  public ApiLocaleResult<?> verificationCodeCheck(@Valid SmsVerificationCodeCheckDto dto) {
    smsFacade.verificationCodeCheck(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete sms.", operationId = "sms:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    smsFacade.delete(ids);
  }

  @Operation(description = "Query the detail of sms.", operationId = "sms:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<SmsDetailVo> detail(
      @Parameter(name = "id", description = "Sms id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(smsFacade.detail(id));
  }

  @Operation(description = "Query the list of sms.", operationId = "sms:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<SmsDetailVo>> list(@Valid SmsFindDto dto) {
    return ApiLocaleResult.success(smsFacade.list(dto));
  }

}
