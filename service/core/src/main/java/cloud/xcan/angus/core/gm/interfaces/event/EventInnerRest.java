package cloud.xcan.angus.core.gm.interfaces.event;


import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.interfaces.event.facade.EventFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EventInner", description = "Used for external systems or services to submit event data programmatically")
@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Validated
@RestController
@RequestMapping("/innerapi/v1/event")
public class EventInnerRest {

  @Resource
  private EventFacade eventFacade;

  @Operation(summary = "Add events")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<EventContent> eventContents) {
    return ApiLocaleResult.success(eventFacade.add(eventContents));
  }
}
