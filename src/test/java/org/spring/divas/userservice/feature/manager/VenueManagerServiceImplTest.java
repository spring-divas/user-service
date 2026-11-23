package org.spring.divas.userservice.feature.manager;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.common.enums.ManagerRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VenueManagerServiceImplTest {

    @Mock private VenueManagerRepository repository;
    @Mock private VenueManagerMapper mapper;
    @InjectMocks private VenueManagerServiceImpl service;

    @Test
    void createInsertsMappedManager() {
        VenueManagerCreateDto request = new VenueManagerCreateDto();
        VenueManager manager = VenueManager.builder().userId(3L).venueId(8L)
                .managerLevel(ManagerRole.SENIOR).build();
        when(mapper.toEntity(request)).thenReturn(manager);
        when(repository.insertIfNotExists(manager)).thenReturn(1);

        service.create(request);

        verify(repository).insertIfNotExists(manager);
    }

    @Test
    void createThrowsWhenManagerAlreadyExists() {
        VenueManagerCreateDto request = new VenueManagerCreateDto();
        VenueManager manager = VenueManager.builder().userId(3L).venueId(8L)
                .managerLevel(ManagerRole.SENIOR).build();
        when(mapper.toEntity(request)).thenReturn(manager);
        when(repository.insertIfNotExists(manager)).thenReturn(0);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void findsAndMapsManagersByUserAndVenue() {
        VenueManager manager = VenueManager.builder().userId(3L).venueId(8L)
                .managerLevel(ManagerRole.JUNIOR).build();
        VenueManagerResponseDto response = VenueManagerResponseDto.builder()
                .userId(3L).venueId(8L).managerLevel(ManagerRole.JUNIOR).build();
        when(repository.findByUserIdAndVenueId(3L, 8L)).thenReturn(List.of(manager));
        when(mapper.toResponseDto(manager)).thenReturn(response);

        assertThat(service.findAllByUserAndVenueId(3L, 8L)).containsExactly(response);
    }

    @Test
    void updateChangesManagerLevelAndSaves() {
        final VenueManager entity = VenueManager.builder().userId(3L).venueId(8L)
                .managerLevel(ManagerRole.JUNIOR).build();
        VenueManagerUpdateDto request = new VenueManagerUpdateDto();
        request.setUserId(3L);
        request.setVenueId(8L);
        request.setManagerLevel(ManagerRole.SENIOR);
        when(repository.findById(new VenueManagerId(3L, 8L))).thenReturn(Optional.of(entity));

        service.update(request);

        assertThat(entity.getManagerLevel()).isEqualTo(ManagerRole.SENIOR);
        verify(repository).save(entity);
    }

    @Test
    void updateThrowsWhenManagerDoesNotExist() {
        VenueManagerUpdateDto request = new VenueManagerUpdateDto();
        request.setUserId(3L);
        request.setVenueId(8L);
        request.setManagerLevel(ManagerRole.SENIOR);
        when(repository.findById(new VenueManagerId(3L, 8L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    void deleteUsesUserAndVenuePair() {
        service.delete(3L, 8L);

        verify(repository).deleteByUserIdAndVenueId(3L, 8L);
    }
}
