package app.core.service;

import app.core.utils.SecurityUtils;
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
public class IncomeServiceImpl extends CommonService implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;

    @Override
    public TransactionBaseResponseDto get(Long id) {
        IncomeEntity income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id + " is not found!"));
        SecurityUtils.checkAccess(income.getUser().getId(), getUserFromSecurityContext().getId());
        return incomeMapper.toResponse(income);
    }

    @Override
    public TransactionBaseResponseDto create(CreateTransactionBaseRequestDto incomeRequest) {
        IncomeEntity income = incomeMapper.createIncomeFromRequest(incomeRequest);
        income.setUser(getUserFromSecurityContext());
        incomeRepository.save(income);
        return incomeMapper.toResponse(income);
    }

    @Override
    public void delete(Long id) {
        IncomeEntity income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        SecurityUtils.checkAccess(income.getUser().getId(), getUserFromSecurityContext().getId());
        incomeRepository.delete(income);
    }

    @Override
    public TransactionBaseResponseDto update(Long id, UpdateTransactionBaseRequestDto incomeRequest) {
        IncomeEntity income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        SecurityUtils.checkAccess(income.getUser().getId(), getUserFromSecurityContext().getId());
        incomeMapper.updateIncomeFromRequest(incomeRequest, income);
        incomeRepository.save(income);

        return incomeMapper.toResponse(income);
    }

    @Override
    public List<TransactionBaseResponseDto> getAllUserIncomes() {
        UserEntity user = getUserFromSecurityContext();
        return incomeRepository.findAllByUserId(user.getId()).stream().map(incomeMapper::toResponse).toList();
    }
}
