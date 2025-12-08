package app.core.service;

import app.core.api.IncomeService;
import app.core.model.IncomeEntity;
import app.core.model.User;
import app.core.repository.IncomeRepository;
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
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Override
    public IncomeEntity get(Long id) {
        IncomeEntity income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id + " is not found!"));
        checkAccess(income);
        return income;
    }

    @Override
    public IncomeEntity create(IncomeEntity incomeEntity) {
        incomeEntity.setUser(getUser());
        return incomeRepository.save(incomeEntity);
    }

    @Override
    public void delete(Long id) {
        IncomeEntity incomeEntity = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        checkAccess(incomeEntity);
        incomeRepository.delete(incomeEntity);
    }

    @Override
    public IncomeEntity update(Long id, IncomeEntity newIncomeEntity) {
        IncomeEntity oldIncomeEntity = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  " is not found!"));
        checkAccess(oldIncomeEntity);

        //TODO: убрать бойлерплейт
        oldIncomeEntity.setAmount(newIncomeEntity.getAmount());
        oldIncomeEntity.setCategory(newIncomeEntity.getCategory());
        oldIncomeEntity.setCreateDate(newIncomeEntity.getCreateDate());
        oldIncomeEntity.setDescription(newIncomeEntity.getDescription());

        return incomeRepository.save(oldIncomeEntity);
    }

    @Override
    public List<IncomeEntity> getAllUserIncomes() {
        User user = getUser();
        return incomeRepository.findAllByUserId(user.getId());
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private void checkAccess(IncomeEntity incomeEntity) {
        User currentUser = getUser();
        if (!incomeEntity.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access to this record is not allowed for current user");
        }
    }

}
