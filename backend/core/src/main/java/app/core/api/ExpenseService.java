package app.core.api;

import app.core.model.ExpenseEntity;

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
    ExpenseEntity get(Long id);


    /**
     * Добавить расход.
     *
     * @param expenseEntity сущность расхода
     * @return расход
     */
    ExpenseEntity create(ExpenseEntity expenseEntity);

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
     * @param expenseEntity расход
     * @return обновленное расхода
     */
    ExpenseEntity update(Long id, ExpenseEntity expenseEntity);

    /**
     * Получить все расходы юзера.
     *
     * @return список расходов юзера
     */
    List<ExpenseEntity> getAllUserExpenses();

}
