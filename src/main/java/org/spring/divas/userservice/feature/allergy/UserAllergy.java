package org.spring.divas.userservice.feature.allergy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_allergy")
@IdClass(UserAllergyId.class)
public class UserAllergy {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "allergen_id", nullable = false)
    private Long allergenId;
}