package com.example.pets_backend.controller;

import com.example.pets_backend.entity.health.*;
import com.example.pets_backend.repository.ThresholdRepository;
import com.example.pets_backend.service.HealthDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Transactional
@Slf4j
public class HealthController {

    private final HealthDataService healthDataService;
    private final ThresholdRepository thresholdRepository;

    @DeleteMapping("/user/pet/health/delete")
    public void deleteData(@RequestParam String data_id) {
        healthDataService.deleteHealthData(data_id);
    }

    @PostMapping("/user/pet/weight/add")
    public HealthData addWeightData(@RequestBody WeightData weightData) {
        healthDataService.deleteSameDateData("WeightData", weightData.getDate());
        return healthDataService.saveWeightData(weightData);
    }

    @PostMapping("/user/pet/calorie/add")
    public HealthData addCalorieData(@RequestBody CalorieData calorieData) {
        healthDataService.deleteSameDateData("CalorieData", calorieData.getDate());
        return healthDataService.saveCalorieData(calorieData);
    }

    @PostMapping("/user/pet/sleep/add")
    public HealthData addSleepData(@RequestBody SleepData sleepData) {
        healthDataService.deleteSameDateData("SleepData", sleepData.getDate());
        return healthDataService.saveSleepData(sleepData);
    }

    @PostMapping("/user/pet/exercise/add")
    public HealthData addExerciseData(@RequestBody ExerciseData exerciseData) {
        healthDataService.deleteSameDateData("ExerciseData", exerciseData.getDate());
        return healthDataService.saveExerciseData(exerciseData);
    }

    @PostMapping("/user/pet/food/add")
    public HealthData addFoodData(@RequestBody FoodData foodData) {
        return healthDataService.saveFoodData(foodData);
    }

    @PostMapping("/user/pet/medi/add")
    public HealthData addMediData(@RequestBody MediData mediData) {
        return healthDataService.saveMediData(mediData);
    }

    @PostMapping("/user/pet/food/edit")
    public HealthData editFoodData (@RequestBody FoodData foodData) {
        return healthDataService.editFoodData(foodData);
    }

    @PostMapping("/user/pet/medi/edit")
    public HealthData editMediData (@RequestBody MediData mediData) {
        return healthDataService.editMediData(mediData);
    }

    @PostMapping("/user/pet/weight")
    public List<HealthData> getWeightDataList (@RequestParam String pet_id, @RequestParam String range) {
        return healthDataService.getHealthData(pet_id, range, "WeightData");
    }

    @PostMapping("/user/pet/calorie")
    public List<HealthData> getCalorieDataList (@RequestParam String pet_id, @RequestParam String range) {
        return healthDataService.getHealthData(pet_id, range, "CalorieData");
    }

    @PostMapping("/user/pet/sleep")
    public List<HealthData> getSleepDataList (@RequestParam String pet_id, @RequestParam String range) {
        return healthDataService.getHealthData(pet_id, range, "SleepData");
    }

    @PostMapping("/user/pet/exercise")
    public List<HealthData> getExerciseDataList (@RequestParam String pet_id, @RequestParam String range) {
        return healthDataService.getHealthData(pet_id, range, "ExerciseData");
    }

    @PostMapping("/user/pet/food")
    public List<HealthData> getFoodDataList (@RequestParam String pet_id, @RequestParam String range) {
        return healthDataService.getHealthData(pet_id, range, "FoodData");
    }

    @PostMapping("/user/pet/medi")
    public List<HealthData> getMediDataList (@RequestParam String pet_id, @RequestParam String range) {
        return healthDataService.getHealthData(pet_id, range, "MediData");
    }

    @PostMapping("/user/pet/health")
    public Map<String, List<HealthData>> getHealthDashboard (@RequestParam String pet_id, @RequestParam String range) {
        return healthDataService.getHealthDashboard(pet_id, range);
    }

    @Transactional
    @PostMapping("/user/pet/weight/alert")
    public void setWeightAlert(@RequestParam String pet_id, @RequestParam int min_thre, @RequestParam int max_thre) {
        Optional<HealthThreshold> thresholdOptional = thresholdRepository.findById(pet_id);
        if (thresholdOptional.isPresent()) {
            HealthThreshold threshold = thresholdOptional.get();
            threshold.setWeight_min(min_thre);
            threshold.setWeight_max(max_thre);
        } else {
            HealthThreshold threshold = new HealthThreshold(pet_id);
            threshold.setWeight_min(min_thre);
            threshold.setWeight_max(max_thre);
            thresholdRepository.save(threshold);
        }
    }

    @Transactional
    @PostMapping("/user/pet/calorie/alert")
    public void setCalorieAlert(@RequestParam String pet_id, @RequestParam int min_thre, @RequestParam int max_thre) {
        Optional<HealthThreshold> thresholdOptional = thresholdRepository.findById(pet_id);
        if (thresholdOptional.isPresent()) {
            HealthThreshold threshold = thresholdOptional.get();
            threshold.setCalorie_min(min_thre);
            threshold.setCalorie_max(max_thre);
        } else {
            HealthThreshold threshold = new HealthThreshold(pet_id);
            threshold.setCalorie_min(min_thre);
            threshold.setCalorie_max(max_thre);
            thresholdRepository.save(threshold);
        }
    }

    @Transactional
    @PostMapping("/user/pet/sleep/alert")
    public void setSleepAlert(@RequestParam String pet_id, @RequestParam int min_thre) {
        Optional<HealthThreshold> thresholdOptional = thresholdRepository.findById(pet_id);
        if (thresholdOptional.isPresent()) {
            HealthThreshold threshold = thresholdOptional.get();
            threshold.setSleep_min(min_thre);
        } else {
            HealthThreshold threshold = new HealthThreshold(pet_id);
            threshold.setSleep_min(min_thre);
            thresholdRepository.save(threshold);
        }
    }

    @Transactional
    @PostMapping("/user/pet/exercise/alert")
    public void setExerciseAlert(@RequestParam String pet_id, @RequestParam int min_thre) {
        Optional<HealthThreshold> thresholdOptional = thresholdRepository.findById(pet_id);
        if (thresholdOptional.isPresent()) {
            HealthThreshold threshold = thresholdOptional.get();
            threshold.setExercise_min(min_thre);
        } else {
            HealthThreshold threshold = new HealthThreshold(pet_id);
            threshold.setExercise_min(min_thre);
            thresholdRepository.save(threshold);
        }
    }

    @PostMapping("/user/pet/alert")
    public LinkedHashMap<String, Integer> getAlertSetting (@RequestParam String pet_id, @RequestParam String type) {
        Optional<HealthThreshold> thresholdOptional = thresholdRepository.findById(pet_id);
        if (thresholdOptional.isEmpty()) {
            throw new EntityNotFoundException("Alert settings not found");
        }
        HealthThreshold threshold = thresholdOptional.get();
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        switch (type) {
            case "weight" -> {
                map.put("min_thre", threshold.getWeight_min());
                map.put("max_thre", threshold.getWeight_max());
            }
            case "calorie" -> {
                map.put("min_thre", threshold.getCalorie_min());
                map.put("max_thre", threshold.getCalorie_max());
            }
            case "sleep" -> {
                map.put("min_thre", threshold.getSleep_min());
                map.put("max_thre", null);
            }
            case "exercise" -> {
                map.put("min_thre", threshold.getExercise_min());
                map.put("max_thre", null);
            }
            default -> throw new IllegalArgumentException("Alert type not recognized");
        }
        return map;
    }
}
