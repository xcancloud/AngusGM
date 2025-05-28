package cloud.xcan.angus.api.gm.policy;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;


@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface AuthPolicyUserRemote {

  @Operation(summary = "Delete the policies authorization of users", operationId = "auth:user:policy:delete:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/api/v1/auth/user/policy")
  void userPolicyDeleteBatch(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "userIds", description = "User ids", required = true)
      @RequestParam(value = "userIds", required = true) HashSet<Long> userIds,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = false)
      @RequestParam(value = "policyIds", required = false) HashSet<Long> policyIds);

}
