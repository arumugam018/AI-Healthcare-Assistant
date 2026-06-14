package com.healthcare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/pages/dashboard.html";
    }

    @GetMapping("/symptom-checker")
    public String symptomChecker() {
        return "redirect:/pages/symptom-checker.html";
    }

    @GetMapping("/chat")
    public String chat() {
        return "redirect:/pages/chat.html";
    }

    @GetMapping("/medicine-reminder")
    public String medicineReminder() {
        return "redirect:/pages/medicine-reminder.html";
    }

    @GetMapping("/history")
    public String history() {
        return "redirect:/pages/history.html";
    }

    @GetMapping("/vision")
    public String vision() {
        return "redirect:/pages/vision.html";
    }
}
 
