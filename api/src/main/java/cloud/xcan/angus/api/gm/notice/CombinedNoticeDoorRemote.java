package cloud.xcan.angus.api.gm.notice;

import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "${xcan.service.gm:ANGUSGM}")
public interface CombinedNoticeDoorRemote {

  @Operation(description = "Send notifications, support SMS, email, and in-site message", operationId = "notice:send")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @PostMapping("/doorapi/v1/notice/combined/send")
  ApiLocaleResult<?> send(@Valid @RequestBody SendNoticeDto dto);

}
