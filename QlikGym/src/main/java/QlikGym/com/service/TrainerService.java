package QlikGym.com.service;

import QlikGym.com.entities.Trainer;
import QlikGym.com.entities.User;
import QlikGym.com.exception.UserNotFoundException;
import QlikGym.com.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {
    @Autowired
    private TrainerRepository trainerRepository;

    // create a trainer
    public Trainer registerTrainer(Trainer trainer, User createdBy) {
        trainer.setCreatedBy(createdBy); // Set the createdBy field
        return trainerRepository.save(trainer); // Save the Trainer object
    }

    // find all trainer
    public List<Trainer> findAllTrainer(){
        List<Trainer> trainerList=trainerRepository.findAll();
        return trainerList;
    }

    // find a trainer by Id
    public Optional<Trainer> findTrainerById(Long trainerId) {
        return trainerRepository.findById(trainerId);
    }
    // delete a trainer
    public void deleteTrainer(Long trainerId) {
        Optional<Trainer> existingTrainer = trainerRepository.findById(trainerId);
        if (existingTrainer.isPresent()) {
            trainerRepository.delete(existingTrainer.get());
        } else {
            throw new UserNotFoundException("Trainer with ID " + trainerId + " not found.");
        }
    }
    // update a trainer
    public Trainer updateTrainer(Long trainerId, Trainer updatedTrainer) {
        Optional<Trainer> existingTrainer = trainerRepository.findById(trainerId);
        if (existingTrainer.isPresent()) {
            Trainer trainer = existingTrainer.get();
            trainer.setName(updatedTrainer.getName());
            trainer.setAppointments(updatedTrainer.getAppointments());
            trainer.setDescription(updatedTrainer.getDescription());
            trainer.setHourlyRate(updatedTrainer.getHourlyRate());
            trainer.setContactInfo(updatedTrainer.getContactInfo());
            return trainerRepository.save(trainer);
        } else {
            throw new UserNotFoundException("Trainer with ID " + trainerId + " not found.");
        }
    }
}
