package com.example.pets_backend.entity.health;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Entity
public class WeightData extends HealthData{

    @Column(length = 5, nullable = false)
    private int weight;     // in kg


    public WeightData(String pet_id, String uid, String date, int weight) {
        super(pet_id, uid, date);
        this.weight = weight;
    }
}
