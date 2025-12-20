package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailListVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Email Management", description = "APIs for email sending, templating, and delivery management")
@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailRest {
    
    private final EmailFacade emailFacade;
    
    @Operation(summary = "Create email", description = "Create a new email for sending")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Email created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<EmailDetailVo> create(@Valid @RequestBody EmailCreateDto dto) {
        return ApiLocaleResult.success(emailFacade.create(dto));
    }
    
    @Operation(summary = "Update email", description = "Update an existing email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @PatchMapping("/{id}")
    public ApiLocaleResult<EmailDetailVo> update(
            @Parameter(description = "Email ID") @PathVariable Long id,
            @Valid @RequestBody EmailUpdateDto dto) {
        dto.setId(id);
        return ApiLocaleResult.success(emailFacade.update(dto));
    }
    
    @Operation(summary = "Send email immediately", description = "Send an email immediately")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<EmailDetailVo> send(@Valid @RequestBody EmailCreateDto dto) {
        return ApiLocaleResult.success(emailFacade.send(dto));
    }
    
    @Operation(summary = "Retry failed email", description = "Retry sending a failed email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email retry initiated successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "400", description = "Cannot retry email in current status")
    })
    @PostMapping("/{id}/retry")
    public ApiLocaleResult<Void> retry(@Parameter(description = "Email ID") @PathVariable Long id) {
        emailFacade.retry(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Cancel pending email", description = "Cancel a pending email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel email in current status")
    })
    @PostMapping("/{id}/cancel")
    public ApiLocaleResult<Void> cancel(@Parameter(description = "Email ID") @PathVariable Long id) {
        emailFacade.cancel(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Delete email", description = "Delete an email message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "Email ID") @PathVariable Long id) {
        emailFacade.delete(id);
        return ApiLocaleResult.success();
    }
    
    @Operation(summary = "Get email details", description = "Get details of a specific email message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found")
    })
    @GetMapping("/{id}")
    public ApiLocaleResult<EmailDetailVo> getById(@Parameter(description = "Email ID") @PathVariable Long id) {
        return ApiLocaleResult.success(emailFacade.findById(id));
    }
    
    @Operation(summary = "List email messages", description = "Get a paginated list of email messages with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email list retrieved successfully")
    })
    @GetMapping
    public ApiLocaleResult<Page<EmailListVo>> list(
            @ParameterObject EmailFindDto dto,
            @ParameterObject Pageable pageable) {
        return ApiLocaleResult.success(emailFacade.findAll(dto, pageable));
    }
    
    @Operation(summary = "Get email statistics", description = "Get email statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email statistics retrieved successfully")
    })
    @GetMapping("/stats")
    public ApiLocaleResult<EmailStatsVo> getStats() {
        return ApiLocaleResult.success(emailFacade.getStats());
    }
}
