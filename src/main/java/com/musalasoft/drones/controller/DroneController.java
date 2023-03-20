package com.musalasoft.drones.controller;

import com.musalasoft.drones.exception.NotFoundException;
import com.musalasoft.drones.model.Drone;
import com.musalasoft.drones.model.Medication;
import com.musalasoft.drones.service.DroneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService service;

    public DroneController(DroneService droneService) {
        this.service = droneService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Drone> registerDrone(@RequestBody Drone drone) {
        try {
            return new ResponseEntity<>(service.registerDrone(drone), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/load/{serialNumber}")
    @ResponseBody
    public ResponseEntity<Drone> loadDrone(@PathVariable String serialNumber, @RequestParam List<Long> medicationIds) {
        try {
            return new ResponseEntity<>(service.loadDrone(serialNumber, medicationIds), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/medications/{serialNumber}")
    @ResponseBody
    public ResponseEntity<List<Medication>> loadedMedications(@PathVariable String serialNumber) {
        try {
            var medications = service.loadedMedications(serialNumber);
            if (medications.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(medications, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available")
    @ResponseBody
    public ResponseEntity<List<Drone>> availableDronesToBeLoaded() {
        try {
            var drones = service.availableDronesToBeLoaded();
            if (drones.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(drones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/batteryCheck/{serialNumber}")
    @ResponseBody
    public ResponseEntity<Integer> droneBatteryLevel(@PathVariable String serialNumber) {
        try {
            var drones = service.droneBatteryLevel(serialNumber);
            return new ResponseEntity<>(drones, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
