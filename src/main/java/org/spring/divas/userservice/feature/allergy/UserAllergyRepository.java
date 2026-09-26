package org.spring.divas.userservice.feature.allergy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, UserAllergyId> {

    List<UserAllergy> findByUserId(Long userId);

    boolean existsByUserIdAndAllergenIdIn(Long userId, List<Long> allergyIds);

    void deleteByUserId(Long userId);
}