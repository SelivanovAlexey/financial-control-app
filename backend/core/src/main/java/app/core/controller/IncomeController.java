package app.core.controller;

import app.core.api.IncomeService;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/incomes")
@Tag(name = "api.incomes.tag", description = "api.incomes.tag.description")
public class IncomeController {

    private final IncomeService incomeService;
    @GetMapping("/{id}")
    @Operation(summary = "api.incomes.get.by.id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.incomes.get.by.id.success"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized"),
            @ApiResponse(responseCode = "403", description = "error.forbidden"),
            @ApiResponse(responseCode = "404", description = "api.incomes.not.found"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<TransactionBaseResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(incomeService.get(id));
    }

    @PostMapping
    @Operation(summary = "api.incomes.create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "api.incomes.create.success"),
            @ApiResponse(responseCode = "400", description = "error.validation"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized"),
            @ApiResponse(responseCode = "403", description = "error.forbidden"),
            @ApiResponse(responseCode = "409", description = "error.conflict"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<TransactionBaseResponseDto> create(@Valid @RequestBody CreateTransactionBaseRequestDto incomeEntity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeService.create(incomeEntity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "api.incomes.delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "api.incomes.delete.success"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized"),
            @ApiResponse(responseCode = "403", description = "error.forbidden"),
            @ApiResponse(responseCode = "404", description = "api.incomes.not.found"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incomeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "api.incomes.update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.incomes.update.success"),
            @ApiResponse(responseCode = "400", description = "error.validation"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized"),
            @ApiResponse(responseCode = "403", description = "error.forbidden"),
            @ApiResponse(responseCode = "404", description = "api.incomes.not.found"),
            @ApiResponse(responseCode = "409", description = "error.conflict"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<TransactionBaseResponseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionBaseRequestDto updateIncomeEntity) {
        return ResponseEntity.ok(incomeService.update(id, updateIncomeEntity));
    }

    @GetMapping
    @Operation(summary = "api.incomes.get.all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.incomes.get.all.success"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized"),
            @ApiResponse(responseCode = "403", description = "error.forbidden"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<List<TransactionBaseResponseDto>> getAllUserIncome() {
        return ResponseEntity.ok(incomeService.getAllUserIncomes());
    }
}
