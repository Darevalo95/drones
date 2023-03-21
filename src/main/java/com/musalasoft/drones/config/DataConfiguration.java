package com.musalasoft.drones.config;

import com.musalasoft.drones.model.Drone;
import com.musalasoft.drones.model.Enum.DroneState;
import com.musalasoft.drones.model.Enum.DroneType;
import com.musalasoft.drones.model.Medication;
import com.musalasoft.drones.repository.DroneRepository;
import com.musalasoft.drones.repository.MedicationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.musalasoft.drones.model.Enum.DroneState.IDLE;
import static com.musalasoft.drones.model.Enum.DroneType.Cruiserweight;
import static com.musalasoft.drones.model.Enum.DroneType.Heavyweight;
import static com.musalasoft.drones.model.Enum.DroneType.Lightweight;
import static com.musalasoft.drones.model.Enum.DroneType.Middleweight;

@Configuration
public class DataConfiguration {

    @Bean
    public CommandLineRunner loadData(DroneRepository droneRepository, MedicationRepository medicationRepository) {
        return (args) -> {
            droneRepository.save(new Drone("EWO567", Lightweight, 80, 80, IDLE, List.of()));
            droneRepository.save(new Drone("POR212", Middleweight, 150, 50, IDLE, List.of()));
            droneRepository.save(new Drone("MVO285", Heavyweight, 220, 20, IDLE, List.of()));
            droneRepository.save(new Drone("QPZ193", Cruiserweight, 300, 10, IDLE, List.of()));

            medicationRepository.save(new Medication(1, "Lisinopril", 100, "TUY577", null));
            medicationRepository.save(new Medication(2, "Amlodipine", 300, "WOT892", null));
            medicationRepository.save(new Medication(3, "Atorvastatin", 20, "ITY246", null));
            medicationRepository.save(new Medication(4, "Metformin", 210, "UWQ547", null));
            medicationRepository.save(new Medication(5, "Omeprazole", 80, "FRT285", null));
        };
    }

}
