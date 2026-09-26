package org.spring.divas.userservice.feature.allergy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAllergyId implements Serializable {

    private Long userId;
    private Long allergenId;
}