package cloud.xcan.angus.core.gm.interfaces.ai;

import cloud.xcan.angus.core.gm.interfaces.ai.facade.AIChatFacade;
import cloud.xcan.angus.core.gm.interfaces.ai.facade.dto.AIChatResultDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AIChat")
@Validated
@RestController
@RequestMapping("/api/v1/ai/chat")
public class AIChatRest {

  @Resource
  private AIChatFacade aiChatFacade;

  @Operation(summary = "Chat and return result", operationId = "ai:chat:result")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Return successfully")})
  @GetMapping("/result")
  public ApiLocaleResult<Map<String, Object>> chatResult(@Valid @ParameterObject AIChatResultDto dto) {
    return ApiLocaleResult.success(aiChatFacade.chatResult(dto));
  }

}
