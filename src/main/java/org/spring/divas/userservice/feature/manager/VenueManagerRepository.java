package org.spring.divas.userservice.feature.manager;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueManagerRepository extends JpaRepository<VenueManager, VenueManagerId> {

    List<VenueManager> findByUserId(Long userId);

    void deleteByUserIdAndVenueId(Long userId, Long venueId);
}