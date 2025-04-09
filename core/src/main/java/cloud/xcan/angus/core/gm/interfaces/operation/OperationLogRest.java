package cloud.xcan.angus.core.gm.interfaces.operation;

import cloud.xcan.angus.core.gm.interfaces.operation.facade.OperationLogFacade;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogSearchDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.vo.OperationLogVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "OperationLog", description = "Provides a unified api for query user operation logs.")
@Validated
@RestController
@RequestMapping("/api/v1/log/operation")
public class OperationLogRest {

  @Resource
  private OperationLogFacade optionFacade;

  @Operation(description = "Query the operation logs list of user.", operationId = "log:operation:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<OperationLogVo>> list(@Valid OperationLogFindDto dto) {
    return ApiLocaleResult.success(optionFacade.list(dto));
  }

  @Operation(description = "Fulltext search the operation logs list of user.", operationId = "log:operation:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<OperationLogVo>> search(@Valid OperationLogSearchDto dto) {
    return ApiLocaleResult.success(optionFacade.search(dto));
  }

}
