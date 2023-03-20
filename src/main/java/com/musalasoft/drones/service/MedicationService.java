package com.musalasoft.drones.service;

import com.musalasoft.drones.exception.NotFoundException;
import com.musalasoft.drones.model.Medication;
import com.musalasoft.drones.repository.MedicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MedicationService.class);

    private final MedicationRepository repository;

    public MedicationService(MedicationRepository medicationRepository) {
        this.repository = medicationRepository;
    }

    public List<Medication> getAll() {
        return repository.findAll();
    }

    public Medication getOneById(long id) {
        var optMedication = repository.findById(id);
        if (optMedication.isEmpty()) {
            LOGGER.error("The Medication with Id {} doesn't exist!", id);
            throw new NotFoundException("The Medication with Id " + id + " doesn't exist!");
        }
        return optMedication.get();
    }

    public Medication update(Medication medication) {
        var id = medication.getId();
        var optMedication = repository.findById(id);
        if (optMedication.isEmpty()) {
            LOGGER.error("The Medication with Id {} doesn't exist!", id);
            throw new NotFoundException("The Medication with Id " + id + " doesn't exist!");
        }
        var oldMedication = optMedication.get();
        oldMedication.setName(medication.getName());
        oldMedication.setWeight(medication.getWeight());
        oldMedication.setImage(medication.getImage());
        return repository.save(optMedication.get());
    }

    public Medication save(Medication medication) {
        return repository.save(medication);
    }

    public void delete(long id) {
        var optMedication = repository.findById(id);
        if (optMedication.isEmpty()) {
            LOGGER.error("The Medication with Id {} doesn't exist!", id);
            throw new NotFoundException("The Medication with Id " + id + " doesn't exist!");
        }
        repository.deleteById(id);
    }

}
