package com.musalasoft.drones.schedules;

import com.musalasoft.drones.service.DroneService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.musalasoft.drones.Utils.createDrone;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
public class DroneTasksTest {

    @Mock
    private DroneService droneService;

    @InjectMocks
    private DroneTasks droneTasks;

    @Test
    void shouldShowDronesWithLowEnergy(CapturedOutput capturedOutput) {
        var drone1 = createDrone("serialNumberTest1", 25);
        var drone2 = createDrone("serialNumberTest2", 10);
        var date = OffsetDateTime.now(Clock.system(ZoneOffset.UTC));

        when(droneService.dronesThatNeedEnergy()).thenReturn(List.of(drone1, drone2));

        try (MockedStatic<OffsetDateTime> mockedStatic = mockStatic(OffsetDateTime.class)) {
            mockedStatic.when(() -> OffsetDateTime.now(Clock.system(ZoneOffset.UTC))).thenReturn(date);

            droneTasks.reportCurrentTime();

            var output = capturedOutput.getOut();

            assertThat(output)
                    .contains("Starting to check drones batteries at " + date.format(DateTimeFormatter.ISO_INSTANT))
                    .contains(drone1.getSerialNumber() + " drone needs to be charged, it has " + drone1.getBattery() + "% of battery")
                    .contains(drone2.getSerialNumber() + " drone needs to be charged, it has " + drone2.getBattery() + "% of battery")
                    .contains("Finish battery check at " + date.format(DateTimeFormatter.ISO_INSTANT));
        }
    }

}
