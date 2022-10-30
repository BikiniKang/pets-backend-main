package com.example.pets_backend.repository;


import com.example.pets_backend.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, String> {
    Species findBySpeciesId(String speciesId);
}
