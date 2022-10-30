package com.example.pets_backend.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Breed {

    @Id
    private final String breedId = NanoIdUtils.randomNanoId();

    private String breedName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "speciesId", nullable = false)
    private Species species;
}