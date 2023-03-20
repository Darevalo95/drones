package com.musalasoft.drones.service;

import com.musalasoft.drones.exception.ServiceException;
import com.musalasoft.drones.model.Drone;
import com.musalasoft.drones.model.Enum.DroneState;
import com.musalasoft.drones.model.Enum.DroneType;
import com.musalasoft.drones.model.Medication;
import com.musalasoft.drones.repository.DroneRepository;
import com.musalasoft.drones.repository.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.musalasoft.drones.model.Enum.DroneState.LOADED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroneServiceTest {
    private static final String SERIAL_NUMBER = "serialNumberTest";

    @Mock
    private DroneRepository droneRepository;
    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private DroneService droneService;

    @Test
    void shouldRegisterADrone() {
        var drone = createDrone();

        when(droneRepository.save(any())).thenReturn(drone);

        var createdDrone = droneService.registerDrone(drone);

        assertThat(createdDrone)
                .usingRecursiveComparison()
                .isEqualTo(drone);
    }

    @Test
    void shouldThrowAnExceptionRegisteringADroneWhenDroneIsEmpty() {
        assertThatThrownBy(() -> droneService.registerDrone(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("An empty drone can't be save");
    }

    @Test
    void shouldThrowAnExceptionRegisteringADroneWhenSerialNumberIsNull() {
        var drone = new Drone();
        assertThatThrownBy(() -> droneService.registerDrone(drone))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("SerialNumber is required to register a Drone.");
    }

    @Test
    void shouldThrowAnExceptionRegisteringADroneWhenSerialNumberIsEmpty() {
        var drone = new Drone();
        drone.setSerialNumber("");
        assertThatThrownBy(() -> droneService.registerDrone(drone))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SerialNumber can't be empty.");
    }

    @Test
    void shouldLoadMedicationsInTheDrone() {
        var drone = createDrone();
        var medication1 = createMedication(1, "medication1", 50);
        var medication2 = createMedication(2, "medication2", 70);
        var totalWeight = drone.getWeight() + medication1.getWeight() + medication2.getWeight();

        when(droneRepository.findById(SERIAL_NUMBER)).thenReturn(Optional.of(drone));
        when(medicationRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(medication1, medication2));
        drone.setState(LOADED);
        when(droneRepository.save(drone)).thenReturn(drone);

        var loadedDrone = droneService.loadDrone(SERIAL_NUMBER, List.of(1L, 2L));

        assertThat(loadedDrone)
                .hasFieldOrPropertyWithValue("weight", totalWeight)
                .hasFieldOrPropertyWithValue("state", DroneState.LOADED)
                .hasFieldOrPropertyWithValue("medications", List.of(medication1, medication2));
    }

    @Test
    void shouldNotLoadMedicationsInTheDroneWhenBatteryIsLessThan25() {
        var drone = createDrone();
        drone.setBattery(15);

        when(droneRepository.findById(SERIAL_NUMBER)).thenReturn(Optional.of(drone));
        drone.setState(LOADED);
        when(droneRepository.save(drone)).thenReturn(drone);

        var loadedDrone = droneService.loadDrone(SERIAL_NUMBER, List.of(1L, 2L));

        assertThat(loadedDrone)
                .hasFieldOrPropertyWithValue("weight", drone.getWeight())
                .hasFieldOrPropertyWithValue("state", DroneState.NEEDS_TO_BE_CHARGED)
                .hasFieldOrPropertyWithValue("medications", List.of());
    }

    @Test
    void shouldNotLoadMedicationsInTheDroneWhenMedicationWeighIsHigherThanAvailable() {
        var drone = createDrone();
        drone.setWeight(470);
        var medication1 = createMedication(1, "medication1", 50);
        var medication2 = createMedication(2, "medication2", 70);

        when(droneRepository.findById(SERIAL_NUMBER)).thenReturn(Optional.of(drone));
        when(medicationRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(medication1, medication2));
        drone.setState(LOADED);
        when(droneRepository.save(drone)).thenReturn(drone);

        var loadedDrone = droneService.loadDrone(SERIAL_NUMBER, List.of(1L, 2L));

        assertThat(loadedDrone)
                .hasFieldOrPropertyWithValue("weight", drone.getWeight())
                .hasFieldOrPropertyWithValue("state", DroneState.LOADED)
                .hasFieldOrPropertyWithValue("medications", List.of());
    }

    @Test
    void shouldReturnLoadedMedicationsOfADrone() {
        var drone = createDrone();
        var medication1 = createMedication(1, "medication1", 50);
        var medication2 = createMedication(2, "medication2", 70);
        drone.addMedication(medication1);
        drone.addMedication(medication2);
        when(droneRepository.findById(SERIAL_NUMBER)).thenReturn(Optional.of(drone));

        var medications = droneService.loadedMedications(SERIAL_NUMBER);

        assertThat(medications)
                .isNotEmpty()
                .hasSameElementsAs(List.of(medication1, medication2));
    }

    @Test
    void shouldThrowAnExceptionWhenDroneDoesNotExistLoadingMedications() {
        when(droneRepository.findById(SERIAL_NUMBER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> droneService.loadedMedications(SERIAL_NUMBER))
                .isInstanceOf(ServiceException.class)
                .hasMessage("The Drone " + SERIAL_NUMBER + " doesn't exist!");
    }

    @Test
    void shouldReturnAvailableDronesToBeLoaded() {
        var drone = createDrone();
        when(droneRepository.findByWeightIsLessThan(anyInt())).thenReturn(List.of(drone));

        var drones = droneService.availableDronesToBeLoaded();

        assertThat(drones)
                .isNotEmpty()
                .hasSameElementsAs(List.of(drone));
    }

    @Test
    void shouldReturnDroneBatteryPercentage() {
        var drone = createDrone();
        when(droneRepository.findById(SERIAL_NUMBER)).thenReturn(Optional.of(drone));

        var batteryLevel = droneService.droneBatteryLevel(SERIAL_NUMBER);

        assertThat(batteryLevel)
                .isEqualTo(drone.getBattery());
    }

    @Test
    void shouldThrowAnExceptionWhenDroneDoesNotExistCheckingTheBatteryLevel() {
        when(droneRepository.findById(SERIAL_NUMBER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> droneService.droneBatteryLevel(SERIAL_NUMBER))
                .isInstanceOf(ServiceException.class)
                .hasMessage("The Drone " + SERIAL_NUMBER + " doesn't exist!");
    }

    private Drone createDrone() {
        return new Drone(SERIAL_NUMBER, DroneType.LIGHTWEIGHT, 50, 100, null, new ArrayList<>());
    }

    private Medication createMedication(long id, String name, int weight) {
        return new Medication(id, name, weight, UUID.randomUUID().toString(), new byte[0]);
    }

}