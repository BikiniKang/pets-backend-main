package com.example.pets_backend.entity.health;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class MediData extends HealthData{

    @Column(nullable = false)
    private String medi_name;

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false)
    private String notes;

    @Column(length = 10, nullable = false)
    private String end_date;    // yyyy-MM-dd

    @ElementCollection
    @CollectionTable(
            name = "medi_time_slots",
            joinColumns = @JoinColumn(name = "data_id")
    )
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<String> time_slots = new ArrayList<>();

    public MediData(String pet_id, String uid, String date, String medi_name, String end_date, List<String> time_slots, String frequency, String notes) {
        super(pet_id, uid, date);
        this.medi_name = medi_name;
        this.end_date = end_date;
        this.time_slots = time_slots;
        this.frequency = frequency;
        this.notes = notes;
    }
}
