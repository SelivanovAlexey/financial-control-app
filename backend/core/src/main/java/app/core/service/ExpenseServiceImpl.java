package app.core.service;

import app.core.api.ExpenseService;
import app.core.model.Expense;
import app.core.model.Income;
import app.core.model.User;
import app.core.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + "is not found!"));
    }

    @Override
    public Expense create(Expense expense) {
        expense.setUser(getUser());
        return expenseRepository.save(expense);
    }

    @Override
    public void delete(Long id) {
        Expense Expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + "is not found!"));
        expenseRepository.delete(Expense);
    }

    @Override
    public Expense update(Long id, Expense newExpense) {
        Expense oldExpense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id +  "is not found!"));
        checkAccess(oldExpense);

        oldExpense.setAmount(newExpense.getAmount());
        oldExpense.setCategory(newExpense.getCategory());
        oldExpense.setCreateDate(newExpense.getCreateDate());

        return expenseRepository.save(oldExpense);
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

    private void checkAccess(Expense expense) {
        User currentUser = getUser();
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access to this record is not allowed for current user");
        }
    }

}
