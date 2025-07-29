package cloud.xcan.angus.core.gm.interfaces.notice;

import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.core.gm.interfaces.notice.facade.NoticeDoorFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "Combined Notice Internal", description = "Internal REST API endpoints for system-initiated message delivery including in-site messages, SMS, and emails")
@Validated
@RestController
@RequestMapping("/innerapi/v1/notice/combined")
public class NoticeCombinedInnerRest {

  @Resource
  NoticeDoorFacade noticeDoorFacade;

  @Operation(summary = "Deliver system notifications", description = "Supports in-site messages, SMS, and email delivery", operationId = "notice:send:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Notifications delivered successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/send")
  public ApiLocaleResult<?> send(@Valid @RequestBody SendNoticeDto dto) {
    noticeDoorFacade.send(dto);
    return ApiLocaleResult.success();
  }

}
