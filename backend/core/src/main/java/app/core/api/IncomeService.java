package app.core.api;

import app.core.model.IncomeEntity;

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
    IncomeEntity get(Long id);


    /**
     * Добавить доход.
     *
     * @param incomeEntity сущность дохода
     * @return доход
     */
    IncomeEntity create(IncomeEntity incomeEntity);

    /**
     * Удалить доход по идентификатору.
     *
     * @param id идентификатор дохода
     */
    void delete(Long id);

    /**
     * Обновить доход по идентификатору.
     *
     * @param id идентификатор дохода
     * @param incomeEntity доход
     * @return обновленное дохода
     */
    IncomeEntity update(Long id, IncomeEntity incomeEntity);

    /**
     * Получить все доходы юзера.
     *
     * @return список доходов юзера
     */
    List<IncomeEntity> getAllUserIncomes();

}
