package app.core.service;

import app.core.security.SecurityProvider;
import app.core.api.ExpenseService;
import app.core.mappers.ExpenseMapper;
import app.core.model.ExpenseEntity;
import app.core.model.UserEntity;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import app.core.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final SecurityProvider securityProvider;

    @Override
    public TransactionBaseResponseDto get(Long id) {
        ExpenseEntity expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + " is not found!"));
        securityProvider.checkAccess(expense.getUser().getId(), securityProvider.getUserFromSecurityContext().getId());
        return expenseMapper.toResponse(expense);
    }

    @Override
    public TransactionBaseResponseDto create(CreateTransactionBaseRequestDto expenseEntity) {
        ExpenseEntity expense = expenseMapper.createExpenseFromRequest(expenseEntity);
        expense.setUser(securityProvider.getUserFromSecurityContext());
        ExpenseEntity savedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    public void delete(Long id) {
        ExpenseEntity expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + " is not found!"));
        securityProvider.checkAccess(expense.getUser().getId(), securityProvider.getUserFromSecurityContext().getId());
        expenseRepository.delete(expense);
    }

    @Override
    public TransactionBaseResponseDto update(Long id, UpdateTransactionBaseRequestDto newExpenseEntity) {
        ExpenseEntity expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        securityProvider.checkAccess(expense.getUser().getId(), securityProvider.getUserFromSecurityContext().getId());

        expenseMapper.updateExpenseFromRequest(newExpenseEntity, expense);
        ExpenseEntity updatedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponse(updatedExpense);
    }

    @Override
    public List<TransactionBaseResponseDto> getAllUserExpenses() {
        UserEntity user = securityProvider.getUserFromSecurityContext();
        return expenseRepository.findAllByUserId(user.getId()).stream().map(expenseMapper::toResponse).toList();
    }
}
