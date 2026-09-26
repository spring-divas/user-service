package org.spring.divas.userservice.feature.manager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VenueManagerRepository extends JpaRepository<VenueManager, VenueManagerId> {

    List<VenueManager> findByUserId(Long userId);

    void deleteByUserIdAndVenueId(Long userId, Long venueId);

    @Modifying
    @Query(value =
            """
            INSERT INTO venue_manager (user_id, venue_id, manager_level)
            VALUES (:#{#entity.userId}, :#{#entity.venueId}, :#{#entity.managerLevel})
            ON CONFLICT (user_id, venue_id) DO NOTHING
            """, nativeQuery = true)
    int insertIfNotExists(@Param("entity") VenueManager entity);
}