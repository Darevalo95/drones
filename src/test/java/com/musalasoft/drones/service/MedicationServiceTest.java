package com.musalasoft.drones.service;

import com.musalasoft.drones.exception.NotFoundException;
import com.musalasoft.drones.repository.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.musalasoft.drones.Utils.createMedication;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceTest {

    @Mock
    private MedicationRepository repository;

    @InjectMocks
    private MedicationService service;

    @Test
    void shouldGetAllMedications() {
        var medication1 = createMedication(1, "medication1", 50);
        var medication2 = createMedication(2, "medication2", 70);
        var expectedMedications = List.of(medication1, medication2);

        when(repository.findAll()).thenReturn(expectedMedications);

        var medications = service.getAll();

        assertThat(medications)
                .isNotEmpty()
                .hasSameElementsAs(expectedMedications);
    }

    @Test
    void shouldGetAllBeEmptyWhenNoMedications() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        var medications = service.getAll();

        assertThat(medications)
                .isEmpty();
    }

    @Test
    void shouldGetOneById() {
        var expectedMedication = createMedication(1, "medication1", 50);

        when(repository.findById(expectedMedication.getId())).thenReturn(Optional.of(expectedMedication));

        var medication = service.getOneById(expectedMedication.getId());

        assertThat(medication)
                .usingRecursiveComparison()
                .isEqualTo(expectedMedication);
    }

    @Test
    void shouldGetOneByIdThrownAnExceptionWhenMedicationDoesNotExist() {
        var id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOneById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("The Medication with Id " + id + " doesn't exist!");
    }

    @Test
    void shouldUpdate() {
        var expectedMedication = createMedication(1, "medication1", 50);

        when(repository.findById(expectedMedication.getId())).thenReturn(Optional.of(expectedMedication));
        when(repository.save(expectedMedication)).thenReturn(expectedMedication);

        var medication = service.update(expectedMedication);

        assertThat(medication)
                .usingRecursiveComparison()
                .isEqualTo(expectedMedication);
    }

    @Test
    void shouldUpdateThrownAnExceptionWhenMedicationDoesNotExist() {
        var medication = createMedication(1, "medication1", 50);

        when(repository.findById(medication.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(medication))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("The Medication with Id " + medication.getId() + " doesn't exist!");
    }

    @Test
    void shouldSave() {
        var expectedMedication = createMedication(1, "medication1", 50);

        when(repository.save(expectedMedication)).thenReturn(expectedMedication);

        var medication = service.save(expectedMedication);

        assertThat(medication)
                .usingRecursiveComparison()
                .isEqualTo(expectedMedication);
    }

    @Test
    void shouldDelete() {
        var expectedMedication = createMedication(1, "medication1", 50);

        when(repository.findById(expectedMedication.getId())).thenReturn(Optional.of(expectedMedication));
        doNothing().when(repository).deleteById(expectedMedication.getId());

        assertThatNoException()
                .isThrownBy(() -> service.delete(expectedMedication.getId()));
    }

    @Test
    void shouldDeleteThrownAnExceptionWhenMedicationDoesNotExist() {
        var id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("The Medication with Id " + id + " doesn't exist!");
    }

}
