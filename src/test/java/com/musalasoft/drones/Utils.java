package com.musalasoft.drones;

import com.musalasoft.drones.model.Drone;
import com.musalasoft.drones.model.Medication;

import java.util.ArrayList;
import java.util.UUID;

import static com.musalasoft.drones.model.Enum.DroneType.Lightweight;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility Class can't be initialized!");
    }

    public static Drone createDrone(String serialNumber, int battery) {
        return new Drone(serialNumber, Lightweight, 80, battery, null, new ArrayList<>());
    }

    public static Medication createMedication(long id, String name, int weight) {
        return new Medication(id, name, weight, UUID.randomUUID().toString(), new byte[0]);
    }

}
