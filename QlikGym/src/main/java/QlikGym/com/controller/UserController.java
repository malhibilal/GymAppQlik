package QlikGym.com.controller;

import QlikGym.com.entities.User;
import QlikGym.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        // getting the user by principal. in this method we are getting the user
        // who is logged in and sending the data to the model.
        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        System.out.println("user: " + user);
        model.addAttribute("user", user);
    }



}
