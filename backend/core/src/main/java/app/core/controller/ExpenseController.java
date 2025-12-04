package app.core.controller;

import app.core.api.ExpenseService;
import app.core.model.Expense;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public Expense get(@PathVariable Long id) {
        return expenseService.get(id);
    }

    @PostMapping
    @Operation(summary = "Добавить расход для пользователя")
    public Expense create(@RequestBody Expense expense) {
        return expenseService.create(expense);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить доход по идентификатору дохода")
    public void delete(@PathVariable Long id) {
        expenseService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить доход по идентификатору дохода")
    public Expense update(@PathVariable Long id, @RequestBody Expense updateExpense) {
        return expenseService.update(id, updateExpense);
    }

    @GetMapping
    @Operation(summary = "Получить информацию о доходах пользователя")
    public List<Expense> getAllUserExpense() {
        return expenseService.getAllUserExpenses();
    }
}
