package app.core.service;

import app.core.security.SecurityProvider;
import app.core.api.IncomeService;
import app.core.mappers.IncomeMapper;
import app.core.model.IncomeEntity;
import app.core.model.UserEntity;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import app.core.repository.IncomeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final SecurityProvider securityProvider;

    @Override
    public TransactionBaseResponseDto get(Long id) {
        IncomeEntity income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id + " is not found!"));
        securityProvider.checkAccess(income.getUser().getId(), securityProvider.getUserFromSecurityContext().getId());
        return incomeMapper.toResponse(income);
    }

    @Override
    public TransactionBaseResponseDto create(CreateTransactionBaseRequestDto incomeRequest) {
        IncomeEntity income = incomeMapper.createIncomeFromRequest(incomeRequest);
        income.setUser(securityProvider.getUserFromSecurityContext());
        IncomeEntity savedIncome = incomeRepository.save(income);
        return incomeMapper.toResponse(savedIncome);
    }

    @Override
    public void delete(Long id) {
        IncomeEntity income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        securityProvider.checkAccess(income.getUser().getId(), securityProvider.getUserFromSecurityContext().getId());
        incomeRepository.delete(income);
    }

    @Override
    public TransactionBaseResponseDto update(Long id, UpdateTransactionBaseRequestDto incomeRequest) {
        IncomeEntity income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        securityProvider.checkAccess(income.getUser().getId(), securityProvider.getUserFromSecurityContext().getId());
        incomeMapper.updateIncomeFromRequest(incomeRequest, income);
        IncomeEntity updatedIncome = incomeRepository.save(income);

        return incomeMapper.toResponse(updatedIncome);
    }

    @Override
    public List<TransactionBaseResponseDto> getAllUserIncomes() {
        UserEntity user = securityProvider.getUserFromSecurityContext();
        return incomeRepository.findAllByUserId(user.getId()).stream().map(incomeMapper::toResponse).toList();
    }
}
