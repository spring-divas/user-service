package org.spring.divas.userservice.feature.allergy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAllergyServiceImpl implements UserAllergyService {

    private final UserAllergyRepository repository;

    @Override
    @Transactional
    public void create(Long userId, Long allergenId) {
        if (repository.existsById(new UserAllergyId(userId, allergenId))) {
            return;
        }
        repository.save(new UserAllergy(userId, allergenId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findAllByUserId(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(UserAllergy::getAllergenId)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyAllergy(Long userId, List<Long> allergyIds) {
        if (allergyIds == null || allergyIds.isEmpty()) {
            return false;
        }
        return repository.existsByUserIdAndAllergenIdIn(userId, allergyIds);
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long allergenId) {
        UserAllergyId id = new UserAllergyId(userId, allergenId);
        repository.findById(id).orElseThrow();
        repository.deleteById(id);
    }
}