package QlikGym.com.service;

import QlikGym.com.entities.Appointment;
import QlikGym.com.entities.Trainer;
import QlikGym.com.entities.User;
import QlikGym.com.repository.AppointmentRepository;
import QlikGym.com.repository.TrainerRepository;
import QlikGym.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerRepository trainerRepository;

   /* Long trainerId, LocalDateTime dateTime, String location,
    String notes,*/

   /* public Appointment bookAppointment(Appointment appointment, Principal principal) {
        // Get the currently logged-in user
        String loggedInUsername = principal.getName();
        User user = userRepository.getUserByUserName(loggedInUsername);

        // Find the trainer by ID
        Optional<Trainer> optionalTrainer = trainerRepository.findById(appointment.getTrainer().getId());

        if (optionalTrainer.isPresent()) {
            Trainer trainer = optionalTrainer.get();

            // Check if the trainer is available at the specified date and time
            boolean isTrainerAvailable = isTrainerAvailable(trainer, appointment.getDateTime());

            if (isTrainerAvailable) {
                // Create and save the appointment
                Appointment appointment1 = new Appointment();
                appointment1.setDateTime(appointment.getDateTime());
                appointment1.setLocation(appointment.getLocation());
                appointment1.setNotes(appointment.getNotes());
                appointment1.setUser(user);
                appointment1.setTrainer(trainer);

                return appointmentRepository.save(appointment1);
            } else {
                throw new IllegalArgumentException("Trainer is not available at the specified date and time.");
            }
        } else {
            throw new NoSuchElementException("Trainer not found.");
        }
    }
*/
    public boolean isTrainerAvailable(Trainer trainer, LocalDateTime dateTime) {
        // Get the list of existing appointments for the trainer
        List<Appointment> trainerAppointments = trainer.getAppointments();

        // Check if there are any conflicting appointments
        for (Appointment appointment : trainerAppointments) {
            LocalDateTime existingAppointmentDateTime = appointment.getDateTime();

            // Define a buffer time (e.g., 1 hour) to allow some flexibility
            Duration bufferTime = Duration.ofHours(1);

            // Check if the new appointment conflicts with an existing appointment
            if (dateTime.isAfter(existingAppointmentDateTime.minus(bufferTime)) &&
                    dateTime.isBefore(existingAppointmentDateTime.plus(bufferTime))) {
                // There is a conflict; the trainer is not available
                return false;
            }
        }

        // No conflicts found; the trainer is available
        return true;
    }

   public boolean bookAppointment(User user, Trainer trainer, LocalDateTime dateTime, String location, String notes) {
       // Check if the trainer is available at the specified date and time
       if (isTrainerAvailable(trainer, dateTime)) {
           Appointment appointment = new Appointment();
           appointment.setDateTime(dateTime);
           appointment.setLocation(location);
           appointment.setNotes(notes);
           appointment.setUser(user);
           appointment.setTrainer(trainer);

           appointmentRepository.save(appointment);
           return true;
       }

       return false;
   }

    public Page<Appointment> getAppointmentsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return appointmentRepository.findAppointmentsByUser(user, pageable);
        } else {
            // Handle the case where the user with the provided ID doesn't exist
            return Page.empty(); // or null or any other appropriate response
        }
    }


}
