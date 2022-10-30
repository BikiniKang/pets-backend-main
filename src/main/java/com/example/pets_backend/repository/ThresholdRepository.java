package com.example.pets_backend.repository;


import com.example.pets_backend.entity.health.HealthThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThresholdRepository extends JpaRepository<HealthThreshold, String> {
}
