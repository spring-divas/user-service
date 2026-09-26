package org.spring.divas.userservice.feature.manager;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.spring.divas.userservice.common.enums.ManagerRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venue_manager")
@IdClass(VenueManagerId.class)
public class VenueManager {

    @Id
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @NotNull
    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "manager_level", nullable = false)
    private ManagerRole managerLevel;
}