package app.core.controller;

import app.core.api.IncomeService;
import app.core.model.IncomeEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для доходов.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/incomes")
@Tag(name = "Контроллер доходов", description = "Контроллер для доходов")
@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
@ApiResponse(responseCode = "401", description = "Ошибка доступа(пользователь не авторизован)")})
public class IncomeController {

    private final IncomeService incomeService;
    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о доходе по id дохода")
    public IncomeEntity get(@PathVariable Long id) {
        return incomeService.get(id);
    }

    @PostMapping
    @Operation(summary = "Добавить доход для пользователя")
    public IncomeEntity create(@RequestBody IncomeEntity incomeEntity) {
        return incomeService.create(incomeEntity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить доход по идентификатору дохода")
    public void delete(@PathVariable Long id) {
        incomeService.delete(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить доход по идентификатору дохода")
    public IncomeEntity update(@PathVariable Long id, @RequestBody IncomeEntity updateIncomeEntity) {
        return incomeService.update(id, updateIncomeEntity);
    }

    @GetMapping
    @Operation(summary = "Получить информацию о доходах пользователя")
    public List<IncomeEntity> getAllUserIncome() {
        return incomeService.getAllUserIncomes();
    }
}
