package QlikGym.com.controller;

import QlikGym.com.entities.Appointment;
import QlikGym.com.entities.Trainer;
import QlikGym.com.entities.User;
import QlikGym.com.helper.Message;
import QlikGym.com.repository.AppointmentRepository;
import QlikGym.com.repository.TrainerRepository;
import QlikGym.com.repository.UserRepository;
import QlikGym.com.service.AppointmentService;
import QlikGym.com.service.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TrainerRepository trainerRepository;


    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        // we have added a query that username= email
        String userName = principal.getName();
        System.out.println("USERNAME " + userName);
        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER " + user);
        model.addAttribute("user", user);

    }

    // dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    @GetMapping("/add-appointment")
    public String openAddAppointmentForm(Model model) {
        Appointment appointment = new Appointment();
        model.addAttribute("title", "Make an Appointment");
        model.addAttribute("appointment", appointment);

        // Fetch the list of available trainers and add it to the model
        List<Trainer> availableTrainers = trainerRepository.findAll(); // You may need to adjust this to fetch the correct list
        model.addAttribute("availableTrainers", availableTrainers);

        return "normal/add_appointment_form";
    }

   /* @PostMapping("/process-appointment")
    public String processAppointment(@ModelAttribute Appointment appointment,
                                     Model model, Principal principal,
                                     @RequestParam("selectedTrainerId") Long selectedTrainerId) {
        //Retrieve the currently logged-in user
       String username = principal.getName();
        User user = userRepository.getUserByUserName(username);

        // Check if a trainer was selected from the dropdown list
        if (selectedTrainerId == null) {
            // Handle the case where no trainer was selected
            model.addAttribute("errorMessage", "Please select a trainer.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
       }

        // Retrieve the selected trainer by ID
        Optional<Trainer> optionalTrainer = trainerRepository.findById(selectedTrainerId);

        if (optionalTrainer.isPresent()) {
           Trainer trainer = optionalTrainer.get();

            // Check trainer availability
            LocalDateTime appointmentDateTime = appointment.getDateTime();
            if (!appointmentService.isTrainerAvailable(trainer, appointmentDateTime)) {
                // Trainer is not available; handle accordingly (e.g., show an error message)
                model.addAttribute("errorMessage", "Trainer is not available at that time.");
                return "normal/add_appointment_form"; // Return to the appointment booking form
            }

            // Trainer is available; create and save the appointment

            appointment.setUser(user);
            appointment.setTrainer(trainer);

            try {
               // appointmentRepository.save(appointment);
                appointmentService.bookAppointment(appointment, principal);
                model.addAttribute("appointment", appointment);
                model.addAttribute("successMessage", "Appointment booked successfully.");
            } catch (IllegalArgumentException e) {
                // Handle the case where the trainer is not available
                model.addAttribute("errorMessage", "Trainer is not available at that time.");
                return "normal/add_appointment_form"; // Return to the appointment booking form
            } catch (NoSuchElementException e) {
                // Handle the case where the trainer is not found (though it should be found if selected by ID)
                model.addAttribute("errorMessage", "Trainer not found.");
                return "normal/add_appointment_form"; // Return to the appointment booking form
            }
        } else {
            // Handle the case where the selected trainer by ID is not found
            model.addAttribute("errorMessage", "Trainer not found.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
        }

        return "normal/add_appointment_form"; // Redirect to the appointment booking form
    }
*/
   @PostMapping("/process-appointment")
   public String processAppointment(@ModelAttribute Appointment appointment,
                                    Model model,
                                    Principal principal,
                                    @RequestParam("selectedTrainerId") Long selectedTrainerId) {
       String username = principal.getName();
       User user = userRepository.getUserByUserName(username);

       if (selectedTrainerId == null) {
           model.addAttribute("errorMessage", "Please select a trainer.");
       } else {
           Optional<Trainer> optionalTrainer = trainerRepository.findById(selectedTrainerId);

           if (optionalTrainer.isPresent()) {
               Trainer trainer = optionalTrainer.get();
               LocalDateTime appointmentDateTime = appointment.getDateTime();

               // Attempt to book the appointment and check if it was successful
               if (appointmentService.bookAppointment(user, trainer, appointmentDateTime, appointment.getLocation(), appointment.getNotes())) {
                   model.addAttribute("successMessage", "Appointment booked successfully.");
               } else {
                   model.addAttribute("errorMessage", "Trainer is not available at that time.");
               }
           } else {
               model.addAttribute("errorMessage", "Trainer not found.");
           }
       }

       // Redirect to the appointment booking form regardless of success or failure
       return "normal/add_appointment_form";
   }

    /* @GetMapping("/add-appointment")
    public String openAddContactForm(Model model) {
        Appointment appointment =new Appointment();
        model.addAttribute("title", "Make an Appointment");
        model.addAttribute("appointment", appointment);

        return "normal/add_appointment_form";
    }*/

    /*@PostMapping("/process-appointment")
    public String processAppointment(@ModelAttribute Appointment appointment, Model model, Principal principal) {
        // Retrieve the currently logged-in user
        String username = principal.getName();
        User user = userRepository.getUserByUserName(username);

        // Retrieve the selected trainer by name (from the form)
        String trainerName = appointment.getTrainer().getName();
      //  Trainer trainer = trainerRepository.findByName(trainerName);
        List<Trainer> trainers = (List<Trainer>) trainerRepository.findByName(trainerName);



        if (trainer == null) {
            // Handle the case where the selected trainer doesn't exist
            model.addAttribute("errorMessage", "Trainer not found.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
        }

        // Check trainer availability
        LocalDateTime appointmentDateTime = appointment.getDateTime();
        if (!appointmentService.isTrainerAvailable( trainer, appointmentDateTime)) {
            // Trainer is not available; handle accordingly (e.g., show an error message)
            model.addAttribute("errorMessage", "Trainer is not available at that time.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
        }

        // Trainer is available; create and save the appointment
        Appointment appointment1 = new Appointment();
        appointment1.setDateTime(appointmentDateTime);
        appointment1.setLocation(appointment.getLocation());
        appointment1.setNotes(appointment.getNotes());
        appointment1.setUser(user);
        appointment1.setTrainer(trainer);

        try {
            appointmentService.bookAppointment(appointment1, principal);
            model.addAttribute("successMessage", "Appointment booked successfully.");
        } catch (IllegalArgumentException e) {
            // Handle the case where the trainer is not available
            model.addAttribute("errorMessage", "Trainer is not available at that time.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
        } catch (NoSuchElementException e) {
            // Handle the case where the trainer is not found
            model.addAttribute("errorMessage", "Trainer not found.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
        }

        return "normal/add_appointment_form"; // Redirect to the appointment booking form
    }

*/
   /* @PostMapping("/process-appointment")
    public String processAppointment(@ModelAttribute Appointment appointment,Model model,
                                 Principal principal, HttpSession session) {
            // reterive the currently logged in user
        String username= principal.getName();
        User user=userRepository.getUserByUserName(username);

        // Retrieve the selected trainer by name (from the form)
        String trainerName = appointment.getTrainer().getName();
        Trainer trainer = trainerRepository.getTrainerByName(trainerName);

        if (trainer == null) {
            // Handle the case where the selected trainer doesn't exist
            model.addAttribute("errorMessage", "Trainer not found.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
        }

        // Check trainer availability
        LocalDateTime appointmentDateTime = appointment.getDateTime();
        if (!appointmentService.isTrainerAvailable(trainer, appointmentDateTime)) {
            // Trainer is not available; handle accordingly (e.g., show an error message)
            model.addAttribute("errorMessage", "Trainer is not available at that time.");
            return "normal/add_appointment_form"; // Return to the appointment booking form
        }

        // Trainer is available; create and save the appointment
        Appointment appointment1 = new Appointment();
        appointment1.setDateTime(appointmentDateTime);
        appointment1.setLocation(appointment.getLocation());
        appointment1.setNotes(appointment.getNotes());
        appointment1.setUser(user);
        appointment1.setTrainer(trainer);

        appointmentService.bookAppointment(appointment1, principal);

        // Redirect to a success page or display a success message
        model.addAttribute("successMessage", "Appointment booked successfully.");


        return "normal/add_appointment_form";
    }

*/

  /* @GetMapping("/show-appointments/{page}")
   public String showAppointments(@PathVariable("page") Integer page,
                                  Model model, Principal principal) {
       // Define the number of appointments to display per page
       int pageSize = 4;

       // Retrieve the currently logged-in user
       String username = principal.getName();
       User user = userRepository.getUserByUserName(username);

       // Calculate the total number of appointments for the user
       long totalAppointments = appointmentRepository.countAppointmentsByUserId((long) user.getId());

       // Calculate the total number of pages based on the number of appointments and the page size
       int totalPages = (int) Math.ceil((double) totalAppointments / pageSize);

       // Ensure the requested page is within bounds
       if (page < 0 || page >= totalPages) {
           // Handle out-of-bounds page request, e.g., redirect to the first page
           return "redirect:/user/show-appointments/0";
       }

       // Create a PageRequest for the current page and page size
       Pageable pageable = PageRequest.of(page, pageSize);

       // Retrieve the appointments for the current page
       Page<Appointment> appointments = appointmentRepository.
               findAppointmentsByUser((long) user.getId(), pageable);

       // Add data to the model
       model.addAttribute("title", "Show User Appointments");
       model.addAttribute("appointments", appointments);
       model.addAttribute("currentPage", page);
       model.addAttribute("totalPages", totalPages);

       return "normal/show_appointments";
   }*/
    @GetMapping("/show-appointments/{page}")
    public String showAppointments(@PathVariable("page") Integer page, Model model,
                               Principal principal) {
        model.addAttribute("title", "Show Appointments");

        // reteriving the user
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        // Define the pagination configuration for trainers created by the admin user
        Pageable pageable = PageRequest.of(page, 4);
        Page<Appointment> appointments = this.appointmentRepository.
                findAppointmentsByUser(user, pageable);

        model.addAttribute("appointments", appointments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointments.getTotalPages());

        return "normal/show_appointments";
    }


    // showing particular contact details.

    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile Page");
        return "normal/profile";
    }
    @GetMapping("/settings")
    public String openSettings() {
        return "normal/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("rewriteNewPassword") String rewriteNewPassword,
                                 Principal principal, HttpSession session) {
        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);

        // Check if the old password matches the one stored in the database
        if (!this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            // Old password does not match; show an error message and redirect to settings
            session.setAttribute("message", new Message("Please enter the correct old password.", "danger"));
            return "redirect:/user/settings";
        }

        // Check if the new password and rewriteNewPassword match
        if (!newPassword.equals(rewriteNewPassword)) {
            // New passwords do not match; show an error message and redirect to settings
            session.setAttribute("message", new Message("New passwords do not match. Please try again.", "danger"));
            return "redirect:/user/settings";
        }

        // Change the password and save it
        currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(currentUser);

        // Show a success message and redirect to the index page
        session.setAttribute("message", new Message("Your password has been successfully changed.", "success"));
        return "redirect:/user/index";
    }


}

