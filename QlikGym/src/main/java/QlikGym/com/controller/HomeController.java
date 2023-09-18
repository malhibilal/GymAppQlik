package QlikGym.com.controller;

import QlikGym.com.controller.helper.MyMessage;
import QlikGym.com.entities.User;
import QlikGym.com.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", " Cosplay QlikGym");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title", " About-CosPlay-QlikGym");
        return "about";
    }

    @GetMapping("/signup")
    public String signUp(Model model){
        model.addAttribute("title", "Sign up");
        model.addAttribute("user", new User());
        return "signup";
    }
    // register a user
    @PostMapping("/register")
    // @ModelAttribute is used when we have to get data from the user ,
    public String registerUser(@Valid @ModelAttribute User user, BindingResult bindingResult,
                               Model model , HttpSession session) {
        try {

            if(bindingResult.hasErrors()) {
                System.out.println("ERROR"+bindingResult.toString());
                model.addAttribute("user",user);
                return "signup";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            model.addAttribute("user", user);
            User result = userRepository.save(user);
            // when all the data has been sent successfully, we will set the user to blank
            model.addAttribute("user", new User());
            // to print the message that it has registered successfully
            session.setAttribute("message",new MyMessage("Registered Successfully","alert-success"));

            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);

            session.setAttribute("message",new MyMessage("something went wrong!! "+e.getMessage(),"alert-danger"));
            return "signup";
        }

    }



    // handler for custom login
    @GetMapping ("/signin")
    public String customlogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }


}
