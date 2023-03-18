package com.musalasoft.drones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "drones")
public class Drone {

    @Id
    @Column(name = "serialNumber")
    private String serialNumber;
    @Column(name = "model")
    private String model;
    @Column(name = "weight")
    private int weight;
    @Column(name = "battery")
    private int battery;
    @Column(name = "state")
    private String state;

}
