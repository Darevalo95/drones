package com.musalasoft.drones.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static com.musalasoft.drones.Utils.createDrone;
import static com.musalasoft.drones.Utils.createMedication;
import static com.musalasoft.drones.model.Enum.DroneState.IDLE;
import static com.musalasoft.drones.model.Enum.DroneState.LOADED;
import static com.musalasoft.drones.service.DroneService.DRONE_BATTERY_LIMIT;
import static com.musalasoft.drones.service.DroneService.DRONE_WEIGHT_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DroneServiceIT {

    @Autowired
    private DroneService service;
    @Autowired
    private MedicationService medicationService;

    @Test
    void shouldRegisterANewDrone() {
        var drone = createDrone(UUID.randomUUID().toString(), 100);
        var createdDrone = service.registerDrone(drone);
        drone.setState(IDLE);

        assertThat(createdDrone)
                .usingRecursiveComparison()
                .isEqualTo(drone);
    }

    @Test
    void shouldLoadTheDroneWithMedications() {
        var serialNumber = UUID.randomUUID().toString();
        var drone = createDrone(serialNumber, 100);
        var createdDrone = service.registerDrone(drone);
        var medication1 = medicationService.save(createMedication(1, "medicationTest1", 20));
        var medication2 = medicationService.save(createMedication(2, "medicationTest2", 70));
        var medication3 = medicationService.save(createMedication(3, "medicationTest3", 500));
        createdDrone.addMedication(medication1);
        createdDrone.addMedication(medication2);
        createdDrone.setState(LOADED);

        var loadedDrone = service.loadDrone(serialNumber, List.of(1L, 2L, 3L));

        assertThat(loadedDrone)
                .usingRecursiveComparison()
                .isEqualTo(createdDrone);

        assertThat(loadedDrone.getMedications())
                .filteredOn(m -> m.getId() == medication3.getId())
                .isEmpty();
    }

    @Test
    void shouldShowMedicationsOfADrone() {
        var serialNumber = UUID.randomUUID().toString();
        var drone = createDrone(serialNumber, 100);
        service.registerDrone(drone);
        medicationService.save(createMedication(1, "medicationTest1", 20));
        medicationService.save(createMedication(2, "medicationTest2", 70));
        var loadedDrone = service.loadDrone(serialNumber, List.of(1L, 2L));

        var loadedMedications = service.loadedMedications(serialNumber);

        assertThat(loadedMedications)
                .usingRecursiveComparison()
                .isEqualTo(loadedDrone.getMedications());
    }

    @Test
    void shouldShowAvailableDronesToBeLoaded() {
        var serialNumber = UUID.randomUUID().toString();
        var drone = createDrone(serialNumber, 100);
        service.registerDrone(drone);

        var drones = service.availableDronesToBeLoaded();

        assertThat(drones)
                .filteredOn(d -> d.getWeight() < DRONE_WEIGHT_LIMIT)
                .isNotEmpty();
    }

    @Test
    void shouldShowDroneBatteryLevel() {
        var serialNumber = UUID.randomUUID().toString();
        var drone = createDrone(serialNumber, 100);
        var createdDrone = service.registerDrone(drone);

        var batteryLevel = service.droneBatteryLevel(createdDrone.getSerialNumber());

        assertThat(batteryLevel)
                .isEqualTo(createdDrone.getBattery());
    }

    @Test
    void shouldShowDronesThatNeedsToBeCharged() {
        var serialNumber = UUID.randomUUID().toString();
        var drone = createDrone(serialNumber, 10);
        service.registerDrone(drone);

        var drones = service.dronesThatNeedEnergy();

        assertThat(drones)
                .filteredOn(d -> d.getBattery() < DRONE_BATTERY_LIMIT)
                .isNotEmpty();
    }

}
