package app.core.service;

import app.core.api.IncomeService;
import app.core.model.Income;
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
    public Income get(Long id) {
        return incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id + "is not found!"));
    }

    @Override
    public Income create(Income income) {
        income.setUser(getUser());
        return incomeRepository.save(income);
    }

    @Override
    public void delete(Long id) {
        Income income = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  "is not found!"));
        incomeRepository.delete(income);
    }

    @Override
    public Income update(Long id, Income newIncome) {
        Income oldIncome = incomeRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Income with id: " + id +  "is not found!"));
        checkAccess(oldIncome);

        oldIncome.setAmount(newIncome.getAmount());
        oldIncome.setCategory(newIncome.getCategory());
        oldIncome.setCreateDate(newIncome.getCreateDate());

        return incomeRepository.save(oldIncome);
    }

    @Override
    public List<Income> getAllUserIncomes() {
        User user = getUser();
        return incomeRepository.findAllByUserId(user.getId());
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private void checkAccess(Income income) {
        User currentUser = getUser();
        if (!income.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access to this record is not allowed for current user");
        }
    }

}
