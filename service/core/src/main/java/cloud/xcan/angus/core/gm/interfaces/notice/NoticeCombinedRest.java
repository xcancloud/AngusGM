package cloud.xcan.angus.core.gm.interfaces.notice;

import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.NoticeCombinedFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Combined Notice", description = "REST API endpoints for user-initiated message delivery including in-site messages, SMS, and emails")
@Validated
@RestController
@RequestMapping("/api/v1/notice/combined")
public class NoticeCombinedRest {

  @Resource
  private NoticeCombinedFacade noticeCombinedFacade;

  @Operation(summary = "Deliver user notifications", description = "Supports in-site messages, SMS, and email delivery", operationId = "notice:send")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Notifications delivered successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/send")
  public ApiLocaleResult<?> send(@Valid @RequestBody SendNoticeDto dto) {
    noticeCombinedFacade.send(dto);
    return ApiLocaleResult.success();
  }

}
