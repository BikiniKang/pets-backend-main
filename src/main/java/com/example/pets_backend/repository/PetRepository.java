package com.example.pets_backend.repository;

import com.example.pets_backend.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {
    Pet findByPetId(String petId);
}
