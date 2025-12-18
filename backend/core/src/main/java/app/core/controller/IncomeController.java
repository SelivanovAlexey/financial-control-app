package app.core.controller;

import app.core.api.IncomeService;
import app.core.model.IncomeEntity;
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
    public TransactionBaseResponseDto get(@PathVariable Long id) {
        return incomeService.get(id);
    }

    @PostMapping
    @Operation(summary = "Добавить доход для пользователя")
    public TransactionBaseResponseDto create(@Valid @RequestBody CreateTransactionBaseRequestDto incomeEntity) {
        return incomeService.create(incomeEntity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить доход по идентификатору дохода")
    public void delete(@PathVariable Long id) {
        incomeService.delete(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменить доход по идентификатору дохода")
    public TransactionBaseResponseDto update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionBaseRequestDto updateIncomeEntity) {
        return incomeService.update(id, updateIncomeEntity);
    }

    @GetMapping
    @Operation(summary = "Получить информацию о доходах пользователя")
    public List<TransactionBaseResponseDto> getAllUserIncome() {
        return incomeService.getAllUserIncomes();
    }
}
