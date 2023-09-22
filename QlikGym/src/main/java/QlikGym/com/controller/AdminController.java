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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

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

    @PostMapping("/process-trainer")
    public String updateTrainer(@ModelAttribute Trainer trainer,
                                @RequestParam("profileImage") MultipartFile file,
                                Model model, Principal principal) {
        // Retrieve the Trainer entity you want to update by its ID
        Long trainerId = trainer.getId();
        Trainer trainer1 = trainerRepository.findById(trainerId).orElse(null);

        if (trainer1 != null) { // Check trainer1, not trainer
            // Update the fields you need
            trainer1.setName(trainer.getName());
            trainer1.setDescription(trainer.getDescription());
            trainer1.setContactInfo(trainer.getContactInfo());
            trainer1.setHourlyRate(trainer.getHourlyRate());
            trainer1.setAvailable(trainer.isAvailable());

            try {
                // Set createdBy based on the currently logged-in admin user
                String adminUserName = principal.getName();
                User adminUser = userRepository.getUserByUserName(adminUserName);
                trainer1.setCreatedBy(adminUser);

                if (!file.isEmpty()) { // Check if the file is not empty
                    // upload the file to the folder and update the name to contact
                    trainer1.setImage(file.getOriginalFilename());
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("image is uploaded");
                }

                // Save the updated Trainer entity
                trainerRepository.save(trainer1); // Save trainer1, not trainer
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Redirect or return a success view
            return "admin/add_trainer_form"; // Redirect to the admin dashboard or another appropriate view
        } else {
            // Handle the case where the trainer with the given ID is not found
            // You may want to show an error message or redirect to an error page
            return "error_page"; // Replace with appropriate error handling
        }
    }



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


    @RequestMapping("/{id}/trainer")
    public String showContactDetail(@PathVariable("id") Long id,
                                    Model model, Principal principal) {
        System.out.println("TID " + id);

        Optional<Trainer> trainerOptional = this.trainerRepository.findById(id);
        Trainer trainer = trainerOptional.get();

        //
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if (user.getId() == trainer.getCreatedBy().getId()) {
            model.addAttribute("trainer", trainer);
            model.addAttribute("title", trainer.getName());
        }

        return "admin/trainer_detail";
    }

    // delete contact handler


    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteTrainer(@PathVariable("id") Long id, HttpSession session,
                                Principal principal) {
        System.out.println("ID" +id);
        Trainer trainer= this.trainerRepository.findById(id).get();

        User user = this.userRepository.getUserByUserName(principal.getName());

        user.getTrainers().remove(trainer);

        this.userRepository.save(user);

        System.out.println("DELETED");
        session.setAttribute("message", new Message("Trainer deleted succesfully...", "success"));

        return "redirect:/admin/show-trainers/0";
    }

    // open update form handler
    @PostMapping("/update-trainer/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("title", "Update Trainer");
        Trainer trainer= this.trainerRepository.findById(id).get();
        model.addAttribute("trainer", trainer);

        return "admin/update_trainer_form";


    }

   @PostMapping("/process-update")
   public String updateHandler(@ModelAttribute Trainer trainer,
                               @RequestParam("profileImage") MultipartFile file,
                               Model model, HttpSession session,
                               Principal principal) {
       try {
           Trainer oldTrainer = this.trainerRepository.findById(trainer.getId()).orElse(null);

           if (oldTrainer != null) {
               // Check if the image file is not empty and oldTrainer.getImage() is not null
               if (!file.isEmpty() && oldTrainer.getImage() != null) {
                   File deleteFile = new ClassPathResource("static/img").getFile();
                   File file1 = new File(deleteFile, oldTrainer.getImage());
                   if (file1.exists()) {
                       file1.delete(); // Delete the old photo file
                   }

                   File saveFile = new ClassPathResource("static/img").getFile();
                   Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                   Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                   trainer.setImage(file.getOriginalFilename());
               }

               User user = this.userRepository.getUserByUserName(principal.getName());
               trainer.setCreatedBy(user);
               this.trainerRepository.save(trainer);
               session.setAttribute("message", new Message("Your trainer is updated", "success"));

               System.out.println("Trainer NAME " + trainer.getName());
               System.out.println("Trainer ID " + trainer.getId());
               return "redirect:/admin/" + trainer.getId() + "/trainer";
           } else {
               // Handle the case where the trainer with the given ID is not found
               // You may want to show an error message or redirect to an error page
               return "redirect:/admin/update-trainer/"+trainer.getId(); // Replace with appropriate error handling
           }
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
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
