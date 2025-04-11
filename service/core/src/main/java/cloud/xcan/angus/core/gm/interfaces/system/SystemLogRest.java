package cloud.xcan.angus.core.gm.interfaces.system;


import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_READ_LINE_NUM;

import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemLogFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request /actuator/systemlog endpoint proxy to solve the CORS problems.
 *
 * @author XiaoLong Liu
 */
@Tag(name = "SystemLog", description = "Provides a unified entry for managing system logs (/actuator/systemlog).")
@Validated
@RestController
@RequestMapping("/api/v1/systemlog")
public class SystemLogRest {

  @Resource
  private SystemLogFacade systemLogFacade;

  @Operation(description = "Query the all log file names of service instance.", operationId = "systemlog:instance:file:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/instance/{id}")
  public ApiLocaleResult<List<String>> fileList(
      @Parameter(name = "id", description = "Instance id", required = true) @PathVariable("id") String instanceId) {
    return ApiLocaleResult.success(systemLogFacade.fileList(instanceId));
  }

  @Operation(description = "Query the content of log file.", operationId = "systemlog:instance:file:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/instance/{id}/file/{name}")
  public ApiLocaleResult<String> fileDetail(
      @Parameter(name = "id", description = "Instance id", required = true) @PathVariable("id") String instanceId,
      @Parameter(name = "name", description = "File name", required = true) @PathVariable("name") String fileName,
      @Parameter(name = "linesNum", description = "Number of log lines, default "
          + DEFAULT_READ_LINE_NUM) @RequestParam("linesNum") Integer linesNum,
      @Parameter(name = "tail", description = "Whether to read from the file end, default true") @RequestParam("tail") Boolean tail) {
    return ApiLocaleResult.successData(
        systemLogFacade.fileDetail(instanceId, fileName, linesNum, tail));
  }

  @Operation(description = "Delete when rolled the log file, otherwise only empty the log file content.", operationId = "systemlog:instance:file:clear")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @PostMapping("/instance/{id}/file/{name}")
  public ApiLocaleResult<?> fileClear(
      @Parameter(name = "id", description = "Instance id", required = true) @PathVariable("id") String instanceId,
      @Parameter(name = "name", description = "File name", required = true) @PathVariable("name") String fileName) {
    systemLogFacade.fileClear(instanceId, fileName);
    return ApiLocaleResult.success();
  }

}
