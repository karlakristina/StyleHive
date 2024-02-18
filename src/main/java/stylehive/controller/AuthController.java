package stylehive.controller;

import stylehive.model.User;
import stylehive.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    UserRepository userRepo;

    @GetMapping("/login")
    public String showLoginForm(Model model){
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/register")
    public String register (Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("success", false);
        return "register";
    }

    @PostMapping("/register_user")
    public String register_user (@Valid User user, BindingResult result, Model model) {
        if (userRepo.findByEmail(user.getEmail()) != null) {
            result.addError(new FieldError("user", "email", "This email address is already used"));
        }

        if (!user.getPassword().equals(user.getPasswordRepeat())) {
            result.addError(new FieldError("user", "passwordRepeat", "The passwords must match"));
            result.addError(new FieldError("user", "password", "The passwords must match"));
        }


        boolean errors = result.hasErrors();
        if (errors) {
            model.addAttribute("user", user);
            model.addAttribute("success", false);
            return "register";
        }


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("guest");
        userRepo.save(user);


        model.addAttribute("user", new User());
        model.addAttribute("success", true);
        return "register";
    }
}
