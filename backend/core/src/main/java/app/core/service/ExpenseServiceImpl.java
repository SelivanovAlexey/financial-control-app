package app.core.service;

import app.core.utils.SecurityUtils;
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
public class ExpenseServiceImpl extends CommonService implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;


    @Override
    public TransactionBaseResponseDto get(Long id) {
        ExpenseEntity expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + " is not found!"));
        SecurityUtils.checkAccess(expense.getUser().getId(), getUserFromSecurityContext().getId());
        return expenseMapper.toResponse(expense);
    }

    @Override
    public TransactionBaseResponseDto create(CreateTransactionBaseRequestDto expenseEntity) {
        ExpenseEntity expense = expenseMapper.createExpenseFromRequest(expenseEntity);
        expense.setUser(getUserFromSecurityContext());
        expenseRepository.save(expense);
        return expenseMapper.toResponse(expense);
    }

    @Override
    public void delete(Long id) {
        ExpenseEntity expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Expense with id: " + id + " is not found!"));
        SecurityUtils.checkAccess(expense.getUser().getId(), getUserFromSecurityContext().getId());
        expenseRepository.delete(expense);
    }

    @Override
    public TransactionBaseResponseDto update(Long id, UpdateTransactionBaseRequestDto newExpenseEntity) {
        ExpenseEntity expense = expenseRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        SecurityUtils.checkAccess(expense.getUser().getId(), getUserFromSecurityContext().getId());

        expenseMapper.updateExpenseFromRequest(newExpenseEntity, expense);
        expenseRepository.save(expense);

        return expenseMapper.toResponse(expense);
    }

    @Override
    public List<TransactionBaseResponseDto> getAllUserExpenses() {
        UserEntity user = getUserFromSecurityContext();
        return expenseRepository.findAllByUserId(user.getId()).stream().map(expenseMapper::toResponse).toList();
    }
}
