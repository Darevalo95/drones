package com.musalasoft.drones.schedules;

import com.musalasoft.drones.model.Drone;
import com.musalasoft.drones.service.DroneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class DroneTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroneTasks.class);

    private final DroneService service;

    public DroneTasks(DroneService droneService) {
        this.service = droneService;
    }

    @Scheduled(fixedRate = 30000)
    public void reportCurrentTime() {
        var startDate = OffsetDateTime.now(Clock.system(ZoneOffset.UTC)).format(DateTimeFormatter.ISO_INSTANT);
        LOGGER.info("Starting to check drones batteries at {}", startDate);
        var drones = service.dronesThatNeedEnergy();
        for (Drone drone : drones) {
            LOGGER.info("{} drone needs to be charged, it has {}% of battery", drone.getSerialNumber(), drone.getBattery());
        }
        var endDate = OffsetDateTime.now(Clock.system(ZoneOffset.UTC)).format(DateTimeFormatter.ISO_INSTANT);
        LOGGER.info("Finish battery check at {}", endDate);
    }

}
