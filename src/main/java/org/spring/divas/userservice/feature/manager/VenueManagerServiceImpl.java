package org.spring.divas.userservice.feature.manager;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueManagerServiceImpl implements VenueManagerService {

    private final VenueManagerRepository repository;

    private final VenueManagerMapper mapper;

    @Override
    @Transactional
    public void create(VenueManagerCreateDto dto) {
        if (repository.existsById(new VenueManagerId(dto.getUserId(), dto.getVenueId()))) {
            throw new EntityExistsException();
        }
        repository.save(mapper.toEntity(dto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueManager> findAllByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void update(VenueManagerUpdateDto dto) {
        VenueManagerId id = new VenueManagerId(dto.getUserId(), dto.getVenueId());
        VenueManager entity = repository.findById(id).orElseThrow();
        entity.setManagerLevel(dto.getManagerLevel());
        repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long venueId) {
        repository.deleteByUserIdAndVenueId(userId, venueId);
    }
}