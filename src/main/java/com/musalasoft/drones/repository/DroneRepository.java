package com.musalasoft.drones.repository;

import com.musalasoft.drones.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, String> {

    List<Drone> findByWeightIsLessThan(int weight);
    List<Drone> findByBatteryLessThanEqual(int batteryLimit);

}
