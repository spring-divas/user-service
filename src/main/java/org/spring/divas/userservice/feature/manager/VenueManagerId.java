package org.spring.divas.userservice.feature.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueManagerId implements Serializable {

    private Long userId;
    private Long venueId;
}