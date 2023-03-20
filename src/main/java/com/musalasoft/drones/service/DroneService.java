package com.musalasoft.drones.service;

import com.musalasoft.drones.exception.NotFoundException;
import com.musalasoft.drones.model.Drone;
import com.musalasoft.drones.model.Enum.DroneState;
import com.musalasoft.drones.model.Medication;
import com.musalasoft.drones.repository.DroneRepository;
import com.musalasoft.drones.repository.MedicationRepository;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.musalasoft.drones.model.Enum.DroneState.IDLE;
import static com.musalasoft.drones.model.Enum.DroneState.LOADED;
import static com.musalasoft.drones.model.Enum.DroneState.LOADING;
import static com.musalasoft.drones.model.Enum.DroneState.NEEDS_TO_BE_CHARGED;
import static java.lang.Boolean.FALSE;

@Service
public class DroneService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DroneService.class);
    private static final int DRONE_WEIGHT_LIMIT = 500;
    private static final int DRONE_BATTERY_LIMIT = 25;

    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;

    public DroneService(@NonNull DroneRepository droneRepository, @NonNull MedicationRepository medicationRepository) {
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
    }

    public Drone registerDrone(Drone drone) {
        Objects.requireNonNull(drone, "An empty drone can't be save");
        Objects.requireNonNull(drone.getSerialNumber(), "SerialNumber is required to register a Drone.");
        if (StringUtils.isEmpty(drone.getSerialNumber())) {
            throw new IllegalArgumentException("SerialNumber can't be empty.");
        }
        return changeState(drone, IDLE);
    }

    public Drone loadDrone(String serialNumber, List<Long> medicationIds) {
        var drone = doesDroneExist(serialNumber);
        if (drone.getBattery() < DRONE_BATTERY_LIMIT) {
            LOGGER.warn("Medications can't be loaded because the battery level of the drone is {}, please charge it!", drone.getBattery());
            return changeState(drone, NEEDS_TO_BE_CHARGED);
        }
        drone = changeState(drone, LOADING);
        var medications = medicationRepository.findAllById(medicationIds);
        for (Medication medication : medications) {
            var totalWeight = drone.getWeight() + medication.getWeight();
            if (totalWeight <= DRONE_WEIGHT_LIMIT) {
                drone.addMedication(medication);
            } else {
                LOGGER.warn("{} couldn't be loaded because it exceeded the available weight ({}gr) of the drone: {}", medication.getName(), DRONE_WEIGHT_LIMIT - drone.getWeight(), drone.getSerialNumber());
            }
        }
        return changeState(drone, LOADED);
    }

    public List<Medication> loadedMedications(String serialNumber) {
        var drone = doesDroneExist(serialNumber);
        return drone.getMedications();
    }

    public List<Drone> availableDronesToBeLoaded() {
        return droneRepository.findByWeightIsLessThan(DRONE_WEIGHT_LIMIT);
    }

    public int droneBatteryLevel(String serialNumber) {
        var drone = doesDroneExist(serialNumber);
        LOGGER.info("Battery level of the drone {} is: {}%", serialNumber, drone.getBattery());
        return drone.getBattery();
    }

    private Drone doesDroneExist(String serialNumber) {
        var optDrone = droneRepository.findById(serialNumber);
        if (optDrone.isEmpty()) {
            LOGGER.error("The Drone {} doesn't exist!", serialNumber);
            throw new NotFoundException("The Drone " + serialNumber + " doesn't exist!");
        }
        return optDrone.get();
    }

    private Drone changeState(Drone drone, DroneState newState) {
        if (FALSE.equals(IDLE.equals(newState))) {
            LOGGER.info("The state of the Drone {} is going to change from {} to {}.", drone.getSerialNumber(), drone.getState().name(), newState.name());
        }
        drone.setState(newState);
        return droneRepository.save(drone);
    }

}
