package app.core.api;

import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;

import java.util.List;


public interface IncomeService {

    TransactionBaseResponseDto get(Long id);

    TransactionBaseResponseDto create(CreateTransactionBaseRequestDto income);

    void delete(Long id);

    TransactionBaseResponseDto update(Long id, UpdateTransactionBaseRequestDto income);

    List<TransactionBaseResponseDto> getAllUserIncomes();

}
