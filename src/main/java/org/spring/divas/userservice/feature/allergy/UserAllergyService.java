package org.spring.divas.userservice.feature.allergy;

import java.util.List;

public interface UserAllergyService {

    void create(Long userId, Long allergenId);

    List<Long> findAllByUserId(Long userId);

    boolean hasAnyAllergy(Long userId, List<Long> allergyId);

    void deleteAllByUserId(Long userId);

    void delete(Long userId, Long allergenId);
}
