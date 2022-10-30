package com.example.pets_backend.entity.health;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HealthThreshold {

    @Id
    private String pet_id;
    private Integer weight_min = null;
    private Integer weight_max = null;
    private Integer calorie_min = null;
    private Integer calorie_max = null;
    private Integer sleep_min = null;
    private Integer exercise_min = null;

    public HealthThreshold (String pet_id) {
        this.pet_id = pet_id;
    }

}
