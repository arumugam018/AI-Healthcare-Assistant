package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/pages/login.html";
    }

    @GetMapping("/signup")
    public String signup() {
        return "redirect:/pages/signup.html";
    }

    @PostMapping("/api/signup")
    public String processSignup(@RequestParam String username, @RequestParam String password) {
        if (userService.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userService.save(user);
            return "redirect:/login?registered=true";
        }
        return "redirect:/signup?error=true";
    }
}
 
