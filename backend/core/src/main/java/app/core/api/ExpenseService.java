package app.core.api;

import app.core.model.Expense;

import java.util.List;

/**
 * Сервис расходов.
 */
public interface ExpenseService {

    /**
     * Получить расход по id.
     *
     * @param id идентификатор расход.
     * @return сущность расход
     */
    Expense get(Long id);


    /**
     * Добавить расход.
     *
     * @param expense сущность расхода
     * @return расход
     */
    Expense create(Expense expense);

    /**
     * Удалить расход по идентификатору.
     *
     * @param id идентификатор расхода
     * @return удаленное расхода
     */
    void delete(Long id);

    /**
     * Обновить расход по идентификатору.
     *
     * @param id идентификатор расхода
     * @param expense расход
     * @return обновленное расхода
     */
    Expense update(Long id, Expense expense);

    /**
     * Получить все расходы юзера.
     *
     * @return список расходов юзера
     */
    List<Expense> getAllUserExpenses();

}
