package com.musalasoft.drones.repository;

import com.musalasoft.drones.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneRepository extends JpaRepository<Drone, String> {
}
