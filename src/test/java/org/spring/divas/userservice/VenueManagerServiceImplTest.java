package org.spring.divas.userservice;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.feature.manager.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueManagerServiceImplTest {

    @Mock
    private VenueManagerRepository repository;

    @Mock
    private VenueManagerMapper mapper;

    @InjectMocks
    private VenueManagerServiceImpl service;

    @Test
    void create_shouldInsertEntitySuccessfully() {
        VenueManagerCreateDto dto = new VenueManagerCreateDto(1L, 2L, ManagerRole.JUNIOR);
        VenueManager entity = new VenueManager(1L, 2L, ManagerRole.JUNIOR);

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.insertIfNotExists(entity)).thenReturn(1);

        service.create(dto);

        verify(repository).insertIfNotExists(entity);
    }

    @Test
    void create_shouldThrowWhenAlreadyExists() {
        VenueManagerCreateDto dto = new VenueManagerCreateDto(1L, 2L, ManagerRole.JUNIOR);
        VenueManager entity = new VenueManager(1L, 2L, ManagerRole.JUNIOR);

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.insertIfNotExists(entity)).thenReturn(0);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void findAllByUserAndVenueId_shouldReturnDtoList() {
        VenueManager entity = new VenueManager(1L, 2L, ManagerRole.SENIOR);
        VenueManagerResponseDto dto = new VenueManagerResponseDto(1L, 2L, ManagerRole.SENIOR);

        when(repository.findByUserIdAndVenueId(1L, 2L)).thenReturn(List.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(dto);

        List<VenueManagerResponseDto> result = service.findAllByUserAndVenueId(1L, 2L);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void update_shouldUpdateManagerLevelWhenFound() {
        VenueManagerUpdateDto dto = new VenueManagerUpdateDto(1L, 2L, ManagerRole.SENIOR);
        VenueManager entity = new VenueManager(1L, 2L, ManagerRole.JUNIOR);
        VenueManagerId id = new VenueManagerId(1L, 2L);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        service.update(dto);

        assertThat(entity.getManagerLevel()).isEqualTo(ManagerRole.SENIOR);
        verify(repository).save(entity);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        VenueManagerUpdateDto dto = new VenueManagerUpdateDto(1L, 2L, ManagerRole.SENIOR);
        VenueManagerId id = new VenueManagerId(1L, 2L);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(dto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void delete_shouldDeleteByUserIdAndVenueId() {
        service.delete(1L, 2L);

        verify(repository).deleteByUserIdAndVenueId(1L, 2L);
    }
}
