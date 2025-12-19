package cloud.xcan.angus.api.gm.user;


import cloud.xcan.angus.api.gm.user.dto.UserAddDto;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserReplaceDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface UserRemote {

  @Operation(summary = "Create new user account", operationId = "user:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User account created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/v1/user")
  ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody UserAddDto dto);

  @Operation(summary = "Update existing user profile information", operationId = "user:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping("/api/v1/user")
  ApiLocaleResult<?> update(@Valid @RequestBody UserUpdateDto dto);

  @Operation(summary = "Replace user profile with complete new information", operationId = "user:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User profile replaced successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PutMapping("/api/v1/user")
  ApiLocaleResult<IdKey<Long, Object>> replace(@Valid @RequestBody UserReplaceDto dto);

  @Operation(summary = "Query the list of user", operationId = "user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/api/v1/user")
  ApiLocaleResult<PageResult<UserDetailVo>> list(@Valid @SpringQueryMap UserFindDto dto);

}
