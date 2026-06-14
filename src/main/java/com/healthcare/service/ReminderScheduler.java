package com.healthcare.service;

import com.healthcare.model.MedicineReminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReminderScheduler {

    private final MedicineReminderService service;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;

    public ReminderScheduler(MedicineReminderService service) {
        this.service = service;
    }

    // Run every minute
    @Scheduled(cron = "0 * * * * *")
    public void sendReminders() {
        if (mailSender == null) return;
        
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        List<MedicineReminder> reminders = service.getAllReminders();
        
        for (MedicineReminder reminder : reminders) {
            if (reminder.getTime() != null && reminder.getTime().equals(currentTime)) {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo("user_placeholder@example.com"); // Require user entity update for dynamic email
                    message.setSubject("CareSync AI - Medicine Reminder!");
                    message.setText("Hello! It's time to take your medicine:\n\n" +
                            "Medicine: " + reminder.getMedicineName() + "\n" +
                            "Dosage: " + reminder.getDosage() + "\n" +
                            "Frequency: " + reminder.getFrequency() + "\n\n" +
                            "Stay healthy,\nCareSync AI");
                    mailSender.send(message);
                    System.out.println("Reminder sent for: " + reminder.getMedicineName());
                } catch (Exception e) {
                    System.err.println("Could not send email. Please check SMTP configuration in application.properties.");
                }
            }
        }
    }
}
 
