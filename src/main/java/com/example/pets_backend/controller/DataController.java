package com.example.pets_backend.controller;

import com.example.pets_backend.entity.Breed;
import com.example.pets_backend.entity.City;
import com.example.pets_backend.entity.Species;
import com.example.pets_backend.repository.BreedRepository;
import com.example.pets_backend.repository.CityRepository;
import com.example.pets_backend.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class DataController {

    private final CityRepository cityRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;

    @PostMapping("/data/location_list/post")
    public void postLocationList(@RequestBody List<String> list) {
        for (String name : list) {
            City city = new City();
            city.setCityName(name);
            cityRepository.save(city);
        }
    }

    @PostMapping("/data/location_list")
    public List<City> getLocationList() {
        return cityRepository.findAll();
    }

    @PostMapping("/data/species_and_breed")
    public void postSpeciesAndBreed(@RequestBody Map<String, List<String>> map) {
        speciesRepository.deleteAll();
        for (Map.Entry<String, List<String>> entry: map.entrySet()) {
            Species species = new Species();
            species.setSpeciesName(entry.getKey());
            Species savedSpecies = speciesRepository.save(species);
            for (String breedName: entry.getValue()) {
                Breed breed = new Breed();
                breed.setBreedName(breedName);
                breed.setSpecies(savedSpecies);
                breedRepository.save(breed);
            }
        }
    }

    @PostMapping("/data/species_list")
    public List<Species> getSpeciesList() {
        return speciesRepository.findAll();
    }

    @PostMapping("/data/breed_list")
    public List<Breed> getBreedList(@RequestBody Map<String, Object> mapIn) {
        String speciesId = (String) mapIn.get("speciesId");
        Species species = speciesRepository.findBySpeciesId(speciesId);
        if (species == null) {
            log.error("Species {} not found in database", speciesId);
            throw new IllegalArgumentException("Species " + speciesId + " not found in database");
        }
        return species.getBreedList();
    }
}
