package app.core.controller;

import app.core.api.ExpenseService;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для расходов.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
@Tag(name = "Контроллер расходов", description = "Контроллер для расходов")
@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
@ApiResponse(responseCode = "401", description = "Ошибка доступа(пользователь не авторизован)")})
public class ExpenseController {

    private final ExpenseService expenseService;
    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о расходе по id дохода")
    public TransactionBaseResponseDto get(@PathVariable Long id) {
        return expenseService.get(id);
    }

    @PostMapping
    @Operation(summary = "Добавить расход для пользователя")
    public TransactionBaseResponseDto create(@Valid @RequestBody CreateTransactionBaseRequestDto expenseEntity) {
        return expenseService.create(expenseEntity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить доход по идентификатору дохода")
    public void delete(@PathVariable Long id) {
        expenseService.delete(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменить доход по идентификатору дохода")
    public TransactionBaseResponseDto update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionBaseRequestDto updateExpenseEntity) {
        return expenseService.update(id, updateExpenseEntity);
    }

    @GetMapping
    @Operation(summary = "Получить информацию о доходах пользователя")
    public List<TransactionBaseResponseDto> getAllUserExpense() {
        return expenseService.getAllUserExpenses();
    }
}
