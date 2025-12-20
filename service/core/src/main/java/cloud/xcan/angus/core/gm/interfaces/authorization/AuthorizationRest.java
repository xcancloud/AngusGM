package cloud.xcan.angus.core.gm.interfaces.authorization;

import cloud.xcan.angus.core.gm.interfaces.authorization.facade.AuthorizationFacade;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
import cloud.xcan.angus.share.view.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Authorization REST Controller
 */
@Tag(name = "Authorization Management", description = "Subject-policy binding management with time-based validity")
@RestController
@RequestMapping("/api/v1/authorizations")
public class AuthorizationRest {
    
    @Resource
    private AuthorizationFacade authorizationFacade;
    
    @Operation(summary = "Create authorization", description = "Grant policy to subject (user, department, or group)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Authorization created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<AuthorizationDetailVo> create(@Valid @RequestBody AuthorizationCreateDto dto) {
        return ApiLocaleResult.success(authorizationFacade.create(dto));
    }
    
    @Operation(summary = "Update authorization", description = "Update authorization information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authorization updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Authorization not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}")
    public ApiLocaleResult<AuthorizationDetailVo> update(
            @Parameter(description = "Authorization ID") @PathVariable Long id,
            @Valid @RequestBody AuthorizationUpdateDto dto) {
        dto.setId(id);
        return ApiLocaleResult.success(authorizationFacade.update(dto));
    }
    
    @Operation(summary = "Enable authorization", description = "Enable an authorization")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authorization enabled successfully"),
        @ApiResponse(responseCode = "404", description = "Authorization not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/enable")
    public ApiLocaleResult<Void> enable(@Parameter(description = "Authorization ID") @PathVariable Long id) {
        authorizationFacade.enable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Disable authorization", description = "Disable an authorization")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authorization disabled successfully"),
        @ApiResponse(responseCode = "404", description = "Authorization not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/disable")
    public ApiLocaleResult<Void> disable(@Parameter(description = "Authorization ID") @PathVariable Long id) {
        authorizationFacade.disable(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Delete authorization", description = "Revoke authorization access")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authorization deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Authorization not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "Authorization ID") @PathVariable Long id) {
        authorizationFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Get authorization details", description = "Get detailed authorization information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authorization retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Authorization not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ApiLocaleResult<AuthorizationDetailVo> getById(@Parameter(description = "Authorization ID") @PathVariable Long id) {
        return ApiLocaleResult.success(authorizationFacade.getById(id));
    }
    
    @Operation(summary = "List authorizations", description = "List authorizations with pagination and filtering")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authorizations retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ApiLocaleResult<Page<AuthorizationListVo>> list(@ParameterObject AuthorizationFindDto dto) {
        return ApiLocaleResult.success(authorizationFacade.find(dto));
    }
    
    @Operation(summary = "Get authorization statistics", description = "Get authorization statistics including counts by status and subject type")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public ApiLocaleResult<AuthorizationStatsVo> getStats() {
        return ApiLocaleResult.success(authorizationFacade.getStats());
    }
}
