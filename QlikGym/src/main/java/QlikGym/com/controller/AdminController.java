package QlikGym.com.controller;


import QlikGym.com.entities.Trainer;
import QlikGym.com.entities.TrainerForm;
import QlikGym.com.entities.User;
import QlikGym.com.helper.Message;
import QlikGym.com.repository.TrainerRepository;
import QlikGym.com.repository.UserRepository;
import QlikGym.com.service.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TrainerService trainerService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
        model.addAttribute("title", "Admin Dashboard");
        return "admin/admin_dashboard";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add-trainer")
    public String openAddContactForm(Model model, Principal principal) {
        String adminUserName = principal.getName();
        User adminUser = this.userRepository.getUserByUserName(adminUserName);

        // Create a new Trainer object
        Trainer trainer = new Trainer();

        // Register the trainer and set the createdBy field
        Trainer newTrainer = trainerService.registerTrainer(trainer, adminUser);

        model.addAttribute("title", "Add Trainer");
        model.addAttribute("trainer", newTrainer);

        return "admin/add_trainer_form";
    }


    // processing add trainer form

    // Handle form submission to add a new Trainer

    // Handle form submission to update a Trainer
  //  @PostMapping("/update-trainer")
    @PostMapping("/process-trainer")
    public String updateTrainer(@ModelAttribute Trainer trainer,
                                Model model, Principal principal) {
        // Retrieve the Trainer entity you want to update by its ID
        Long trainerId = trainer.getId();
        Trainer trainer1 = trainerRepository.findById(trainerId).orElse(null);

        if (trainer != null) {
            // Update the fields you need
            trainer1.setName(trainer.getName());
            trainer1.setDescription(trainer.getDescription());
            trainer1.setContactInfo(trainer.getContactInfo());
            trainer1.setHourlyRate(trainer.getHourlyRate());
            trainer1.setAvailable(trainer.isAvailable());

            // Set createdBy based on the currently logged-in admin user
            String adminUserName = principal.getName();
            User adminUser = userRepository.getUserByUserName(adminUserName);
            trainer1.setCreatedBy(adminUser);

            // Save the updated Trainer entity
            trainerRepository.save(trainer1);
        }

        // Redirect or return a success view
        return "admin/add_trainer_form"; // Redirect to the admin dashboard or another appropriate view
    }

    /*@PostMapping("/process-trainer")
    public String addTrainer(@ModelAttribute TrainerForm trainerForm, Model model, Principal principal) {
        // Create a new Trainer object
        Trainer trainer = new Trainer();

        // Populate Trainer properties with form data
        trainer.setName(trainerForm.getName());
        trainer.setDescription(trainerForm.getDescription());
        trainer.setContactInfo(trainerForm.getContactInfo());
        trainer.setHourlyRate(trainerForm.getHourlyRate());
        trainer.setAvailable(trainerForm.isAvailable());

        // Set createdBy based on the currently logged-in admin user
        String adminUserName = principal.getName();
        User adminUser = userRepository.getUserByUserName(adminUserName);
        trainer.setCreatedBy(adminUser);

        // Save the Trainer object to the database
        // Save the Trainer object to the database using the repository's custom save method
        trainerRepository.saveTrainer(trainer.getId(), trainer.getName(),
                trainer.getDescription(), trainer.getContactInfo(),
                trainer.getHourlyRate(), trainer.isAvailable(), trainer.getCreatedBy());




        // Redirect or return a success view
       // return "redirect:/admin/index";
        // Redirect to the admin dashboard or another appropriate view
        return "admin/add_trainer_form";
    }
*/
   /* @PostMapping("/process-trainer")
    public String processContact(Trainer trainer, Model model, HttpSession session) {
        try {
            Trainer newTrainer = trainerRepository.save(trainer);
            System.out.println("DATA " + newTrainer);
            System.out.println("Added to the database");
            model.addAttribute("trainer", newTrainer);

            // Set a success message
            session.setAttribute("successMessage", new Message("Your Trainer is added!! Add more..", "success"));
        } catch (Exception e) {
            e.printStackTrace();
            // Set an error message
            session.setAttribute("errorMessage", new Message("Something went wrong!! Try again..", "danger"));
        }

        return "admin/add_trainer_form";
    }
*/
    // show contacts handler
    // per page = 5[n]
    // current page = 0 [page]
    @GetMapping("/show-trainers/{page}")
    public String showTrainers(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "Show Trainers");
        String adminUserName = principal.getName();

        // Retrieve the admin user
        User adminUser = this.userRepository.getUserByUserName(adminUserName);

        // Define the pagination configuration for trainers created by the admin user
        Pageable pageable = PageRequest.of(page, 4);

        // Retrieve a Page of trainers created by the admin user
        Page<Trainer> trainers = this.trainerRepository.findTrainersByCreatedBy(adminUser, pageable);

        model.addAttribute("trainer", trainers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", trainers.getTotalPages());

        return "admin/show_trainers";
    }

    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile Page");
        return "admin/profile";
    }
    @GetMapping("/settings")
    public String openSettings() {
        return "admin/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
        System.out.println("OLD PASSWORD " + oldPassword);
        System.out.println("NEW PASSWORD " + newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());

        if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            // change the password

            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Your password is successfully changed..", "success"));

        } else {
            // error...
            session.setAttribute("message", new Message("Please Enter correct old password !!", "danger"));
            return "redirect:/admin/settings";
        }

        return "redirect:/admin/index";
    }

}
