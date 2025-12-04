package app.core.api;

import app.core.model.Income;

import java.util.List;

/**
 * Сервис доходов.
 */
public interface IncomeService {

    /**
     * Получить доход по id.
     *
     * @param id идентификатор доход.
     * @return сущность дохода
     */
    Income get(Long id);


    /**
     * Добавить доход.
     *
     * @param income сущность дохода
     * @return доход
     */
    Income create(Income income);

    /**
     * Удалить доход по идентификатору.
     *
     * @param id идентификатор дохода
     * @return удаленное дохода
     */
    void delete(Long id);

    /**
     * Обновить доход по идентификатору.
     *
     * @param id идентификатор дохода
     * @param income доход
     * @return обновленное дохода
     */
    Income update(Long id, Income income);

    /**
     * Получить все доходы юзера.
     *
     * @return список доходов юзера
     */
    List<Income> getAllUserIncomes();

}
