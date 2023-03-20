package com.musalasoft.drones;

import com.musalasoft.drones.model.Medication;

import java.util.UUID;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility Class can't be initialized!");
    }

    public static Medication createMedication(long id, String name, int weight) {
        return new Medication(id, name, weight, UUID.randomUUID().toString(), new byte[0]);
    }

}
