package cloud.xcan.angus.api.gm.group;

import cloud.xcan.angus.api.gm.group.dto.GroupFindDto;
import cloud.xcan.angus.api.gm.group.vo.GroupDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface GroupRemote {

  @Operation(summary = "Query group list", operationId = "group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/api/v1/group")
  ApiLocaleResult<PageResult<GroupDetailVo>> list(@Valid @SpringQueryMap GroupFindDto dto);

  @Operation(summary = "Search user list", operationId = "user:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/api/v1/group/search")
  ApiLocaleResult<PageResult<GroupDetailVo>> search(@Valid @SpringQueryMap GroupFindDto dto);

}
