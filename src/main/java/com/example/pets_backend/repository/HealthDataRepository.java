package com.example.pets_backend.repository;


import com.example.pets_backend.entity.health.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, String> {

    @Query("SELECT w.data_id FROM WeightData w where w.date = ?1")
    String findWeightIdByDate(String date);

    @Query("SELECT c.data_id FROM CalorieData c where c.date = ?1")
    String findCalorieIdByDate(String date);

    @Query("SELECT s.data_id FROM SleepData s where s.date = ?1")
    String findSleepIdByDate(String date);

    @Query("SELECT e.data_id FROM ExerciseData e where e.date = ?1")
    String findExerciseIdByDate(String date);

    @Query("SELECT w FROM WeightData w where w.pet_id = ?1 and w.date > ?2 order by w.date")
    List<HealthData> getWeightData (String pet_id, String startFrom);

    @Query("SELECT w FROM CalorieData w where w.pet_id = ?1 and w.date > ?2 order by w.date")
    List<HealthData> getCalorieData (String pet_id, String startFrom);

    @Query("SELECT w FROM SleepData w where w.pet_id = ?1 and w.date > ?2 order by w.date")
    List<HealthData> getSleepData (String pet_id, String startFrom);

    @Query("SELECT w FROM ExerciseData w where w.pet_id = ?1 and w.date > ?2 order by w.date")
    List<HealthData> getExerciseData (String pet_id, String startFrom);

    @Query("SELECT w FROM FoodData w where w.pet_id = ?1 and w.date > ?2 order by w.date")
    List<HealthData> getFoodData (String pet_id, String startFrom);

    @Query("SELECT w FROM MediData w where w.pet_id = ?1 and w.date > ?2")
    List<HealthData> getMediData (String pet_id, String startFrom);
}
