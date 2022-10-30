package com.example.pets_backend.entity.health;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Entity
public class FoodData extends HealthData{

    @Column(nullable = false)
    private String food_name;

    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private String notes;

    public FoodData(String pet_id, String uid, String date, String food_name, String amount, String notes) {
        super(pet_id, uid, date);
        this.food_name = food_name;
        this.amount = amount;
        this.notes = notes;
    }
}
