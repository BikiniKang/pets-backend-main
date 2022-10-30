package com.example.pets_backend.service;

import com.example.pets_backend.entity.health.*;
import com.example.pets_backend.repository.HealthDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HealthDataService {

    private final PetService petService;
    private final HealthDataRepository healthDataRepo;

    public HealthData saveWeightData(WeightData weightData) {
        if (weightData.getWeight() == 0) {
            throw new IllegalArgumentException("Weight cannot be 0 or null");
        }
        return saveHealthData(weightData);
    }

    public HealthData saveCalorieData(CalorieData calorieData) {
        if (calorieData.getCalorie() == 0) {
            throw new IllegalArgumentException("Calorie cannot be 0 or null");
        }
        return saveHealthData(calorieData);
    }

    public HealthData saveSleepData (SleepData sleepData) {
        if (sleepData.getDuration_str() == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }
        sleepData.setMinutes();
        return saveHealthData(sleepData);
    }

    public HealthData saveExerciseData (ExerciseData exerciseData) {
        if (exerciseData.getExercise_type() == null) {
            throw new IllegalArgumentException("Exercise type cannot be null");
        }
        if (exerciseData.getDuration_str() == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }
        exerciseData.setMinutes();
        return saveHealthData(exerciseData);
    }

    public HealthData saveFoodData (FoodData foodData) {
        if (foodData.getFood_name() == null) {
            throw new IllegalArgumentException("Food name cannot be null");
        }
        if (foodData.getAmount() == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (foodData.getNotes() == null) {
            throw new IllegalArgumentException("Notes cannot be null, you can set it as 'NA'");
        }
        return saveHealthData(foodData);
    }

    public HealthData saveMediData (MediData mediData) {
        if (mediData.getMedi_name() == null) {
            throw new IllegalArgumentException("Medication name cannot be null");
        }
        if (mediData.getFrequency() == null) {
            throw new IllegalArgumentException("Frequency cannot be null");
        }
        if (mediData.getNotes() == null) {
            throw new IllegalArgumentException("Notes cannot be null, you can set it as 'NA'");
        }
        if (mediData.getEnd_date() == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        return saveHealthData(mediData);
    }

    public void deleteHealthData(String data_id) {
        healthDataRepo.deleteById(data_id);
    }

    public void deleteSameDateData (String className, String date) {
        String dupDateId;
        switch (className) {
            case "WeightData" -> dupDateId = healthDataRepo.findWeightIdByDate(date);
            case "CalorieData" -> dupDateId = healthDataRepo.findCalorieIdByDate(date);
            case "SleepData" -> dupDateId = healthDataRepo.findSleepIdByDate(date);
            case "ExerciseData" -> dupDateId = healthDataRepo.findExerciseIdByDate(date);
            default -> throw new IllegalArgumentException("className not recognized");
        }
        if (dupDateId != null) {
            healthDataRepo.deleteById(dupDateId);
        }
    }

    public HealthData editFoodData(FoodData foodData) {
        if (!healthDataRepo.existsById(foodData.getData_id())) {
            throw new IllegalArgumentException("Missing or invalid data_id");
        }
        return saveFoodData(foodData);
    }

    public HealthData editMediData(MediData mediData) {
        if (!healthDataRepo.existsById(mediData.getData_id())) {
            throw new IllegalArgumentException("Missing or invalid data_id");
        }
        return saveMediData(mediData);
    }

    public List<HealthData> getHealthData (String pet_id, String range, String className) {
        petService.checkIfPetIdInDB(pet_id);
        String startFrom = getDateStartFrom(range);
        return switch (className) {
            case "WeightData" -> healthDataRepo.getWeightData(pet_id, startFrom);
            case "CalorieData" -> healthDataRepo.getCalorieData(pet_id, startFrom);
            case "SleepData" -> healthDataRepo.getSleepData(pet_id, startFrom);
            case "ExerciseData" -> healthDataRepo.getExerciseData(pet_id, startFrom);
            case "FoodData" -> healthDataRepo.getFoodData(pet_id, startFrom);
            case "MediData" -> healthDataRepo.getMediData(pet_id, startFrom);
            default -> throw new IllegalArgumentException("className not recognized");
        };
    }

    public Map<String, List<HealthData>> getHealthDashboard (String pet_id, String range) {
        petService.checkIfPetIdInDB(pet_id);
        String startFrom = getDateStartFrom(range);
        Map<String, List<HealthData>> map = new HashMap<>();
        map.put("weight_list", healthDataRepo.getWeightData(pet_id, startFrom));
        map.put("calorie_list", healthDataRepo.getCalorieData(pet_id, startFrom));
        map.put("sleep_list", healthDataRepo.getSleepData(pet_id, startFrom));
        map.put("exercise_list", healthDataRepo.getExerciseData(pet_id, startFrom));
        map.put("food_list", healthDataRepo.getFoodData(pet_id, startFrom));
        map.put("medi_list", healthDataRepo.getMediData(pet_id, startFrom));
        return map;
    }

    private HealthData saveHealthData(HealthData healthData) {
        if (healthData.getPet_id() == null) {
            throw new IllegalArgumentException("pet_id cannot be null");
        }
        if (healthData.getDate() == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        try {
            LocalDate date = LocalDate.parse(healthData.getDate());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid format of date; It should be 'yyyy-MM-dd'");
        }
        healthData.setPet(petService.findByPetId(healthData.getPet_id()));
        return healthDataRepo.save(healthData);
    }

    private String getDateStartFrom (String range) {
        LocalDate today = LocalDate.now();
        return switch (range) {
            case "All" -> "2000-01-01";
            case "Week" -> today.minusDays(7).toString();
            case "Month" -> today.minusMonths(1).toString();
            case "6Month" -> today.minusMonths(6).toString();
            case "Year" -> today.minusYears(1).toString();
            default -> throw new IllegalArgumentException("Range not recognized");
        };
    }

}
