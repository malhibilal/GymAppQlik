package QlikGym.com.controller;

import QlikGym.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

        @GetMapping("/dashboard")
        public String dashboard(Model model, Principal principal) {
            // Get the logged-in user's role
            String role = userRepository.findByEmail(principal.getName()).getRole();

            // Check the user's role and redirect accordingly
            if ("ROLE_ADMIN".equals(role)) {
                // Admin role, redirect to admin dashboard
                return "redirect:/admin/dashboard";
            } else {
                // Normal user role, redirect to user dashboard
                return "redirect:/user/index";
            }
        }

    }


