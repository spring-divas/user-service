package org.spring.divas.userservice.feature.allergy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAllergyServiceImplTest {

    @Mock private UserAllergyRepository repository;
    @InjectMocks private UserAllergyServiceImpl service;

    @Test
    void createStoresUserAndAllergenPair() {
        service.create(3L, 12L);

        verify(repository).save(new UserAllergy(3L, 12L));
    }

    @Test
    void findAllByUserReturnsAllergenIds() {
        when(repository.findByUserId(3L)).thenReturn(List.of(
                new UserAllergy(3L, 12L), new UserAllergy(3L, 15L)));

        assertThat(service.findAllByUserId(3L)).containsExactly(12L, 15L);
    }

    @Test
    void hasAnyAllergyQueriesTheRepository() {
        when(repository.existsByUserIdAndAllergenIdIn(3L, List.of(12L, 15L))).thenReturn(true);

        assertThat(service.hasAnyAllergy(3L, List.of(12L, 15L))).isTrue();
    }

    @Test
    void hasAnyAllergyReturnsFalseForNullOrEmptyListWithoutQuerying() {
        assertThat(service.hasAnyAllergy(3L, null)).isFalse();
        assertThat(service.hasAnyAllergy(3L, List.of())).isFalse();

        verify(repository, never()).existsByUserIdAndAllergenIdIn(any(), any());
    }

    @Test
    void deleteAllByUserDeletesEveryAllergyForThatUser() {
        service.deleteAllByUserId(3L);

        verify(repository).deleteByUserId(3L);
    }

    @Test
    void deleteRequiresAnExistingUserAllergy() {
        UserAllergyId id = new UserAllergyId(3L, 12L);
        when(repository.findById(id)).thenReturn(Optional.of(new UserAllergy(3L, 12L)));

        service.delete(3L, 12L);

        verify(repository).deleteById(id);
    }

    @Test
    void deleteThrowsWhenUserAllergyDoesNotExist() {
        UserAllergyId id = new UserAllergyId(3L, 12L);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(3L, 12L))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }
}
