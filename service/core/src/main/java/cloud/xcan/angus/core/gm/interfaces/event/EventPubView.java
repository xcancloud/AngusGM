package cloud.xcan.angus.core.gm.interfaces.event;

import cloud.xcan.angus.core.gm.interfaces.event.facade.EventFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Event Public View", description = "HTML-based visualization endpoints for viewing event details in browser-rendered format")
@Validated
@Controller
@RequestMapping("/pubview/v1/event")
public class EventPubView {

  @Resource
  private EventFacade eventFacade;

  @Operation(summary = "Retrieve event details in HTML format", operationId = "event:detail:html")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Event HTML content rendered successfully")})
  @GetMapping("/{id}")
  public String detail(Model mode,
      @Parameter(name = "id", description = "Event identifier", required = true) @PathVariable("id") Long id) {
    EventDetailVo event = eventFacade.detail(id);
    mode.addAttribute("e", event);
    mode.addAttribute("s", event.getSource());
    return "/event";
  }

}
