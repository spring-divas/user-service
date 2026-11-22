package org.spring.divas.userservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.feature.allergy.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAllergyServiceImplTest {

    @Mock
    private UserAllergyRepository repository;

    @InjectMocks
    private UserAllergyServiceImpl allergyService;

    @Test
    void create_shouldSaveUserAllergy() {
        allergyService.create(1L, 5L);

        verify(repository).save(any(UserAllergy.class));
    }

    @Test
    void findAllByUserId_shouldReturnListOfAllergenIds() {
        UserAllergy allergy1 = new UserAllergy(1L, 10L);
        UserAllergy allergy2 = new UserAllergy(1L, 20L);
        when(repository.findByUserId(1L)).thenReturn(List.of(allergy1, allergy2));

        List<Long> result = allergyService.findAllByUserId(1L);

        assertThat(result).containsExactly(10L, 20L);
    }

    @Test
    void hasAnyAllergy_shouldReturnFalseForEmptyOrNullList() {
        assertThat(allergyService.hasAnyAllergy(1L, null)).isFalse();
        assertThat(allergyService.hasAnyAllergy(1L, List.of())).isFalse();
    }

    @Test
    void hasAnyAllergy_shouldDelegateToRepository() {
        when(repository.existsByUserIdAndAllergenIdIn(1L, List.of(5L, 6L))).thenReturn(true);

        boolean result = allergyService.hasAnyAllergy(1L, List.of(5L, 6L));

        assertThat(result).isTrue();
    }

    @Test
    void deleteAllByUserId_shouldDelete() {
        allergyService.deleteAllByUserId(1L);

        verify(repository).deleteByUserId(1L);
    }

    @Test
    void delete_shouldDeleteWhenFound() {
        UserAllergyId id = new UserAllergyId(1L, 5L);
        UserAllergy allergy = new UserAllergy(1L, 5L);
        when(repository.findById(id)).thenReturn(Optional.of(allergy));

        allergyService.delete(1L, 5L);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        UserAllergyId id = new UserAllergyId(1L, 5L);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> allergyService.delete(1L, 5L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
