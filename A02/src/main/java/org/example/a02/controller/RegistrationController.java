package org.example.a02.controller;

import jakarta.validation.Valid;
import org.example.a02.dto.UserRegistrationDto;
import org.example.a02.enums.Role;
import org.example.a02.model.User;
import org.example.a02.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public RegistrationController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("User") UserRegistrationDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes
    ) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "User registered successfully");
        return "redirect:/login";
    }
}
