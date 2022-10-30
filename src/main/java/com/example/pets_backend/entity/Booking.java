package com.example.pets_backend.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    private String booking_id = NanoIdUtils.randomNanoId();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uid", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_uid"))
    private User user;

    @NonNull
    private String uid;

    @ElementCollection
    @CollectionTable(
            name = "booking_petidlist",
            joinColumns = @JoinColumn(name = "booking_id")
    )
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<String> pet_id_list = new ArrayList<>();

    @NonNull
    @Column(length = 32)
    private String attendee;    // email of the request sender/recipient

    @NonNull
    @Column(length = 64)
    private String title;

    @NonNull
    @Column(length = 16)
    private String start_time;  // YYYY-MM-DD HH:mm

    @NonNull
    @Column(length = 16)
    private String end_time;    // YYYY-MM-DD HH:mm

    @NonNull
    private String location;

    @NonNull
    private String description;

    @NonNull
    @Column(length = 16)
    private String status;

    @NonNull
    private Boolean request_sender; // true if the user is the request sender, false if the user is the recipient

    private String pair_bk_id;  // the booking id shown in invitee/invitor 's database

    public List<LinkedHashMap<String, Object>> getPetAbList() {
        List<LinkedHashMap<String, Object>> petAbList = new ArrayList<>();
        for (String petId:this.pet_id_list) {
            petAbList.add(this.user.getPetByPetId(petId).getPetAb());
        }
        return petAbList;
    }
}
