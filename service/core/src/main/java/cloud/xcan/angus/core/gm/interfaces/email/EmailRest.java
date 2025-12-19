package cloud.xcan.angus.core.gm.interfaces.email;

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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
@Tag(name = "Email Management", description = "APIs for email sending, templating, and delivery management")
public class EmailRest {
    
    private final EmailFacade emailFacade;
    
    @PostMapping
    @Operation(summary = "Create email", description = "Create a new email for sending")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmailDetailVo> create(@RequestBody EmailCreateDto dto) {
        EmailDetailVo vo = emailFacade.create(dto);
        return ResponseEntity.ok(vo);
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Update email", description = "Update an existing email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email updated successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmailDetailVo> update(
            @Parameter(description = "Email ID") @PathVariable Long id,
            @RequestBody EmailUpdateDto dto) {
        EmailDetailVo vo = emailFacade.update(id, dto);
        return ResponseEntity.ok(vo);
    }
    
    @PostMapping("/send")
    @Operation(summary = "Send email immediately", description = "Send an email immediately")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> send(@RequestBody EmailCreateDto dto) {
        EmailDetailVo email = emailFacade.create(dto);
        emailFacade.send(email.getId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry failed email", description = "Retry sending a failed email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email retry initiated"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "400", description = "Email cannot be retried"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> retry(@Parameter(description = "Email ID") @PathVariable Long id) {
        emailFacade.retry(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel pending email", description = "Cancel a pending email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "400", description = "Email cannot be cancelled"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> cancel(@Parameter(description = "Email ID") @PathVariable Long id) {
        emailFacade.cancel(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete email", description = "Delete an email record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "Email ID") @PathVariable Long id) {
        emailFacade.delete(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get email details", description = "Get detailed information about an email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email found"),
            @ApiResponse(responseCode = "404", description = "Email not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmailDetailVo> findById(@Parameter(description = "Email ID") @PathVariable Long id) {
        EmailDetailVo vo = emailFacade.findById(id);
        return ResponseEntity.ok(vo);
    }
    
    @GetMapping
    @Operation(summary = "List emails", description = "Get list of emails with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emails retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EmailListVo>> findAll(
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by type") @RequestParam(required = false) String type,
            @Parameter(description = "Filter by recipient") @RequestParam(required = false) String recipient,
            @Parameter(description = "Filter by subject") @RequestParam(required = false) String subject) {
        EmailFindDto dto = new EmailFindDto();
        dto.setStatus(status);
        dto.setType(type);
        dto.setRecipient(recipient);
        dto.setSubject(subject);
        List<EmailListVo> vos = emailFacade.findAll(dto);
        return ResponseEntity.ok(vos);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get email statistics", description = "Get email statistics including counts by status and type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmailStatsVo> getStatistics() {
        EmailStatsVo vo = emailFacade.getStatistics();
        return ResponseEntity.ok(vo);
    }
}
