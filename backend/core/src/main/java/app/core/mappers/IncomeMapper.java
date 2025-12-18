package app.core.mappers;

import app.core.model.IncomeEntity;
import app.core.model.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IncomeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    IncomeEntity createIncomeFromRequest(CreateTransactionBaseRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIncomeFromRequest(UpdateTransactionBaseRequestDto request, @MappingTarget IncomeEntity incomeEntity);

    TransactionBaseResponseDto toResponse(IncomeEntity incomeEntity);
}
