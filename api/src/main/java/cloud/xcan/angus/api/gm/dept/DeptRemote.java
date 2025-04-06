package cloud.xcan.angus.api.gm.dept;

import cloud.xcan.angus.api.gm.dept.dto.DeptFindDto;
import cloud.xcan.angus.api.gm.dept.dto.DeptSearchDto;
import cloud.xcan.angus.api.gm.dept.vo.DeptDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "${xcan.service.gm:ANGUSGM}")
public interface DeptRemote {

  @Operation(description = "Query dept list", operationId = "dept:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/api/v1/dept")
  ApiLocaleResult<PageResult<DeptDetailVo>> list(@Valid @SpringQueryMap DeptFindDto dto);

  @Operation(description = "Search dept list", operationId = "dept:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/api/v1/dept/search")
  ApiLocaleResult<PageResult<DeptDetailVo>> search(@Valid @SpringQueryMap DeptSearchDto dto);


}
