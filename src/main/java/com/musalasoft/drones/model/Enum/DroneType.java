package com.musalasoft.drones.model.Enum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DroneType {

    LIGHTWEIGHT("Lightweight"),
    MIDDLEWEIGHT("Middleweight"),
    CRUISERWEIGHT("Cruiserweight"),
    HEAVYWEIGHT("Heavyweight");

    private final String name;

}
