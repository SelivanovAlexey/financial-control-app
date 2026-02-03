package app.core.controller;

import app.core.api.ExpenseService;
import app.core.errorhandling.model.CommonExceptionJson;
import app.core.errorhandling.model.ValidationExceptionJson;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
@Tag(name = "api.expenses.tag", description = "api.expenses.tag.description")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/{id}")
    @Operation(summary = "api.expenses.get.by.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.expenses.get.by.id.success",
                    content = @Content(schema = @Schema(implementation = TransactionBaseResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "error.unauthorized",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "403", description = "error.forbidden",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "404", description = "api.expenses.not.found",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "500", description = "error.internal.server",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class)))
    })
    public ResponseEntity<TransactionBaseResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.get(id));
    }

    @PostMapping
    @Operation(summary = "api.expenses.create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "api.expenses.create.success",
                    content = @Content(schema = @Schema(implementation = TransactionBaseResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "error.validation",
                    content = @Content(schema = @Schema(implementation = ValidationExceptionJson.class))),
            @ApiResponse(responseCode = "401", description = "error.unauthorized",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "403", description = "error.forbidden",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "409", description = "error.conflict",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "500", description = "error.internal.server",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class)))
    })
    public ResponseEntity<TransactionBaseResponseDto> create(@Valid @RequestBody CreateTransactionBaseRequestDto expenseEntity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(expenseEntity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "api.expenses.delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "api.expenses.delete.success"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "403", description = "error.forbidden",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "404", description = "api.expenses.not.found",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "500", description = "error.internal.server",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "api.expenses.update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.expenses.update.success",
                    content = @Content(schema = @Schema(implementation = TransactionBaseResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "error.validation",
                    content = @Content(schema = @Schema(implementation = ValidationExceptionJson.class))),
            @ApiResponse(responseCode = "401", description = "error.unauthorized",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "403", description = "error.forbidden",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "404", description = "api.expenses.not.found",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "409", description = "error.conflict",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "500", description = "error.internal.server",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class)))
    })
    public ResponseEntity<TransactionBaseResponseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionBaseRequestDto updateExpenseEntity) {
        return ResponseEntity.ok(expenseService.update(id, updateExpenseEntity));
    }

    @GetMapping
    @Operation(summary = "api.expenses.get.all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.expenses.get.all.success",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "error.unauthorized",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "403", description = "error.forbidden",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class))),
            @ApiResponse(responseCode = "500", description = "error.internal.server",
                    content = @Content(schema = @Schema(implementation = CommonExceptionJson.class)))
    })
    public ResponseEntity<List<TransactionBaseResponseDto>> getAllUserExpense() {
        return ResponseEntity.ok(expenseService.getAllUserExpenses());
    }
}