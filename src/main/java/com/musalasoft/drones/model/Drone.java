package com.musalasoft.drones.model;

import com.musalasoft.drones.model.Enum.DroneState;
import com.musalasoft.drones.model.Enum.DroneType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drones")
public class Drone {

    @Id
    @Column(name = "serialNumber")
    private String serialNumber;
    @Column(name = "model")
    private DroneType model;
    @Column(name = "weight")
    private int weight;
    @Column(name = "battery")
    private int battery;
    @Column(name = "state")
    private DroneState state;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "medications_drones",
            joinColumns = @JoinColumn(name = "serialNumber"),
            inverseJoinColumns = @JoinColumn(name = "medication_id")
    )
    private List<Medication> medications;

    public void addMedication(Medication medication) {
        this.medications.add(medication);
        this.weight += medication.getWeight();
    }

    public void setBattery(int battery) {
        if (battery > 100) {
            throw new IllegalArgumentException("Battery value is a percentage, can't be more than 100%");
        }
        this.battery = battery;
    }

}
