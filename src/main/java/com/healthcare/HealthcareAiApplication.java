package com.healthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:application.properties")
public class HealthcareAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcareAiApplication.class, args);
    }
    
    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner commandLineRunner(org.springframework.context.ApplicationContext ctx) {
        return args -> {
            System.out.println("====== CLASSPATH CHECK ======");
            java.net.URL url = HealthcareAiApplication.class.getClassLoader().getResource("application.properties");
            System.out.println("application.properties URL: " + url);
            System.out.println("=============================");
            
            System.out.println("====== BEAN INSPECTION ======");
            System.out.println("FirebaseConfig loaded? " + ctx.containsBean("firebaseConfig"));
            System.out.println("SecurityConfig loaded? " + ctx.containsBean("securityConfig"));
            System.out.println("UserService loaded? " + ctx.containsBean("userService"));
            System.out.println("GeminiService loaded? " + ctx.containsBean("geminiService"));
            System.out.println("=============================");
        };
    }
}
 
