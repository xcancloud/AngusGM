package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.common.result.ApiLocaleResult;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsListVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
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

@Tag(name = "SMS Management", description = "SMS management APIs")
@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsRest {

    private final SmsFacade smsFacade;

    @Operation(summary = "Create SMS", description = "Create a new SMS message")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "SMS created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<SmsDetailVo> create(@Valid @RequestBody SmsCreateDto dto) {
        return ApiLocaleResult.success(smsFacade.create(dto));
    }

    @Operation(summary = "Send SMS", description = "Send an SMS message immediately")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "SMS sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiLocaleResult<SmsDetailVo> send(@Valid @RequestBody SmsCreateDto dto) {
        return ApiLocaleResult.success(smsFacade.send(dto));
    }

    @Operation(summary = "Update SMS", description = "Update an existing SMS message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "SMS not found")
    })
    @PatchMapping("/{id}")
    public ApiLocaleResult<SmsDetailVo> update(
            @Parameter(description = "SMS ID") @PathVariable Long id,
            @Valid @RequestBody SmsUpdateDto dto) {
        dto.setId(id);
        return ApiLocaleResult.success(smsFacade.update(dto));
    }

    @Operation(summary = "Retry SMS", description = "Retry sending a failed SMS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS retry initiated successfully"),
            @ApiResponse(responseCode = "404", description = "SMS not found"),
            @ApiResponse(responseCode = "400", description = "Cannot retry SMS in current status")
    })
    @PostMapping("/{id}/retry")
    public ApiLocaleResult<Void> retry(@Parameter(description = "SMS ID") @PathVariable Long id) {
        smsFacade.retry(id);
        return ApiLocaleResult.success();
    }

    @Operation(summary = "Cancel SMS", description = "Cancel a pending SMS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "SMS not found"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel SMS in current status")
    })
    @PostMapping("/{id}/cancel")
    public ApiLocaleResult<Void> cancel(@Parameter(description = "SMS ID") @PathVariable Long id) {
        smsFacade.cancel(id);
        return ApiLocaleResult.success();
    }

    @Operation(summary = "Delete SMS", description = "Delete an SMS message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS deleted successfully"),
            @ApiResponse(responseCode = "404", description = "SMS not found")
    })
    @DeleteMapping("/{id}")
    public ApiLocaleResult<Void> delete(@Parameter(description = "SMS ID") @PathVariable Long id) {
        smsFacade.delete(id);
        return ApiLocaleResult.success();
    }

    @Operation(summary = "Get SMS details", description = "Get details of a specific SMS message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "SMS not found")
    })
    @GetMapping("/{id}")
    public ApiLocaleResult<SmsDetailVo> getById(@Parameter(description = "SMS ID") @PathVariable Long id) {
        return ApiLocaleResult.success(smsFacade.findById(id));
    }

    @Operation(summary = "List SMS messages", description = "Get a paginated list of SMS messages with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS list retrieved successfully")
    })
    @GetMapping
    public ApiLocaleResult<Page<SmsListVo>> list(
            @ParameterObject SmsFindDto dto,
            @ParameterObject Pageable pageable) {
        return ApiLocaleResult.success(smsFacade.findAll(dto, pageable));
    }

    @Operation(summary = "Get SMS statistics", description = "Get SMS statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SMS statistics retrieved successfully")
    })
    @GetMapping("/stats")
    public ApiLocaleResult<SmsStatsVo> getStats() {
        return ApiLocaleResult.success(smsFacade.getStats());
    }
}
