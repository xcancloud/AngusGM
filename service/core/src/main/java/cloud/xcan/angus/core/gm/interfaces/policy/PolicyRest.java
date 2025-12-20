package cloud.xcan.angus.core.gm.interfaces.policy;

import cloud.xcan.angus.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.PolicyFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyCreateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.PolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyStatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Policy Management", description = "Policy management APIs")
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyRest {

    private final PolicyFacade policyFacade;

    @Operation(summary = "Create policy", description = "Create a new policy")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Policy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Policy already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<PolicyDetailVo> create(@Valid @RequestBody PolicyCreateDto dto) {
        return ApiLocaleResult.success(policyFacade.create(dto));
    }

    @Operation(summary = "Update policy", description = "Update an existing policy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Policy updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @PatchMapping("/{id}")
    public ApiLocaleResult<PolicyDetailVo> update(
            @Parameter(description = "Policy ID") @PathVariable Long id,
            @Valid @RequestBody PolicyUpdateDto dto) {
        dto.setId(id);
        return ApiLocaleResult.success(policyFacade.update(dto));
    }

    @Operation(summary = "Enable policy", description = "Enable a policy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Policy enabled successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(@Parameter(description = "Policy ID") @PathVariable Long id) {
        policyFacade.enable(id);
        return ApiLocaleResult.success();
    }

    @Operation(summary = "Disable policy", description = "Disable a policy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Policy disabled successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(@Parameter(description = "Policy ID") @PathVariable Long id) {
        policyFacade.disable(id);
        return ApiLocaleResult.success();
    }

    @Operation(summary = "Delete policy", description = "Delete a policy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Policy deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "Policy ID") @PathVariable Long id) {
        policyFacade.delete(id);
        return ApiLocaleResult.success();
    }

    @Operation(summary = "Get policy details", description = "Get policy details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @GetMapping("/{id}")
    public ApiLocaleResult<PolicyDetailVo> getById(@Parameter(description = "Policy ID") @PathVariable Long id) {
        return ApiLocaleResult.success(policyFacade.getById(id));
    }

    @Operation(summary = "List policies", description = "Get policy list with filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping
    public ApiLocaleResult<Page<PolicyListVo>> find(@ParameterObject PolicyFindDto dto) {
        return ApiLocaleResult.success(policyFacade.find(dto));
    }

    @Operation(summary = "Get policy statistics", description = "Get policy statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping("/stats")
    public ApiLocaleResult<PolicyStatsVo> getStats() {
        return ApiLocaleResult.success(policyFacade.getStats());
    }
}
