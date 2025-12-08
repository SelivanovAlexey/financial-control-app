package app.core.service;

import app.core.api.ExpenseService;
import app.core.model.ExpenseEntity;
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
    public ExpenseEntity get(Long id) {
        ExpenseEntity entity = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + " is not found!"));
        checkAccess(entity);
        return entity;
    }

    @Override
    public ExpenseEntity create(ExpenseEntity expenseEntity) {
        expenseEntity.setUser(getUser());
        return expenseRepository.save(expenseEntity);
    }

    @Override
    public void delete(Long id) {
        ExpenseEntity entity = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + " is not found!"));
        checkAccess(entity);
        expenseRepository.delete(entity);
    }

    @Override
    public ExpenseEntity update(Long id, ExpenseEntity newExpenseEntity) {
        ExpenseEntity oldExpenseEntity = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id +  " is not found!"));
        checkAccess(oldExpenseEntity);

        //TODO: убрать бойлерплейт
        oldExpenseEntity.setAmount(newExpenseEntity.getAmount());
        oldExpenseEntity.setCategory(newExpenseEntity.getCategory());
        oldExpenseEntity.setCreateDate(newExpenseEntity.getCreateDate());
        oldExpenseEntity.setDescription(newExpenseEntity.getDescription());

        return expenseRepository.save(oldExpenseEntity);
    }

    @Override
    public List<ExpenseEntity> getAllUserExpenses() {
        User user = getUser();
        return expenseRepository.findAllByUserId(user.getId());
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private void checkAccess(ExpenseEntity expenseEntity) {
        User currentUser = getUser();
        if (!expenseEntity.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access to this record is not allowed for current user");
        }
    }

}
