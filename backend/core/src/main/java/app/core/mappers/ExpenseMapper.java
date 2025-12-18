package app.core.mappers;

import app.core.model.ExpenseEntity;
import app.core.model.IncomeEntity;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    ExpenseEntity createExpenseFromRequest(CreateTransactionBaseRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExpenseFromRequest(UpdateTransactionBaseRequestDto request, @MappingTarget ExpenseEntity expenseEntity);

    TransactionBaseResponseDto toResponse(ExpenseEntity expenseEntity);
}
