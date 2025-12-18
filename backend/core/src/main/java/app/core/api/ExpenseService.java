package app.core.api;

import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;

import java.util.List;

public interface ExpenseService {

    TransactionBaseResponseDto get(Long id);

    TransactionBaseResponseDto create(CreateTransactionBaseRequestDto expenseEntity);

    void delete(Long id);

    TransactionBaseResponseDto update(Long id, UpdateTransactionBaseRequestDto expenseEntity);

    List<TransactionBaseResponseDto> getAllUserExpenses();

}
