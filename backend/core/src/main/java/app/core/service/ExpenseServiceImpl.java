package app.core.service;

import app.core.api.ExpenseService;
import app.core.model.Expense;
import app.core.model.User;
import app.core.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Реализация сервис доходов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public Expense get(Long id) {
        return expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Не найдено дохода по id: " + id));
    }

    @Override
    public Expense create(Expense expense) {
        expense.setCreateDate(OffsetDateTime.now());
        expense.setUser(getUser());
        return expenseRepository.save(expense);
    }

    @Override
    public void delete(Long id) {
        Expense Expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Не найдено дохода по id: " + id));
        expenseRepository.delete(Expense);
    }

    @Override
    public Expense update(Long id, Expense newExpense) {
        newExpense.setId(id);
        newExpense.setCreateDate(OffsetDateTime.now());
        return expenseRepository.saveAndFlush(newExpense);
    }

    @Override
    public List<Expense> getAllUserExpenses() {
        User user = getUser();
        return expenseRepository.findAllByUserId(user.getId());
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}
